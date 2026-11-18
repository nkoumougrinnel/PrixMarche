"""API Flask de PrixMarché."""

import os
from datetime import datetime
from pathlib import Path

import pandas as pd
from flask import Flask, jsonify, request

try:
    from .catalogue import MARCHES, PRODUITS
except ImportError:
    from catalogue import MARCHES, PRODUITS


API_DIR = Path(__file__).resolve().parent
REQUIRED_COLUMNS = ["semaine", "produit", "marche", "prix_fcfa", "unite", "source"]


def _chemin_csv():
    configured_path = os.environ.get("PRIXMARCHE_CSV")
    if configured_path:
        path = Path(configured_path)
        return path if path.is_absolute() else API_DIR / path

    synthetic_path = API_DIR.parent / "data" / "prix_synthetiques.csv"
    if synthetic_path.exists():
        return synthetic_path
    return API_DIR.parent / "data" / "exemple_prix.csv"


CSV_PATH = _chemin_csv()
DONNEES_PRIX = pd.read_csv(CSV_PATH)
missing_columns = [column for column in REQUIRED_COLUMNS if column not in DONNEES_PRIX.columns]
if missing_columns:
    raise ValueError(
        f"CSV de prix invalide ({CSV_PATH}) : colonnes manquantes : "
        f"{', '.join(missing_columns)}"
    )

app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify(statut="ok")


@app.get("/products")
def products():
    origine = "fictives" if DONNEES_PRIX["source"].eq("synthetique").all() else "mixtes"
    return jsonify(produits=PRODUITS, marches=MARCHES, donnees=origine)


@app.get("/prices")
def prices():
    produit = request.args.get("produit")
    marche = request.args.get("marche")
    if not produit:
        return jsonify(erreur="Le paramètre obligatoire 'produit' est manquant."), 400
    if not marche:
        return jsonify(erreur="Le paramètre obligatoire 'marche' est manquant."), 400

    product_ids = {item["id"] for item in PRODUITS}
    market_ids = {item["id"] for item in MARCHES}
    if produit not in product_ids:
        return jsonify(erreur=f"Produit inconnu : '{produit}'."), 400
    if marche not in market_ids:
        return jsonify(erreur=f"Marché inconnu : '{marche}'."), 400

    depuis = request.args.get("depuis")
    if "depuis" in request.args:
        try:
            date_depuis = datetime.strptime(depuis, "%Y-%m-%d").date()
        except ValueError:
            return jsonify(
                erreur="Le paramètre 'depuis' doit être une date valide au format AAAA-MM-JJ."
            ), 400
        if date_depuis.isoformat() != depuis:
            return jsonify(
                erreur="Le paramètre 'depuis' doit être une date valide au format AAAA-MM-JJ."
            ), 400

    lignes = DONNEES_PRIX.loc[
        (DONNEES_PRIX["produit"] == produit) & (DONNEES_PRIX["marche"] == marche)
    ].copy()
    lignes["_semaine_date"] = pd.to_datetime(lignes["semaine"], format="%Y-%m-%d")
    if "depuis" in request.args:
        lignes = lignes.loc[lignes["_semaine_date"] >= pd.Timestamp(date_depuis)]
    lignes = lignes.sort_values("_semaine_date")

    prix = [
        {
            "semaine": ligne["_semaine_date"].strftime("%Y-%m-%d"),
            "produit": ligne["produit"],
            "marche": ligne["marche"],
            "prix_fcfa": int(ligne["prix_fcfa"]),
            "unite": ligne["unite"],
            "source": ligne["source"],
        }
        for _, ligne in lignes.iterrows()
    ]
    origine = "fictives" if all(ligne["source"] == "synthetique" for ligne in prix) else "mixtes"
    return jsonify(produit=produit, marche=marche, donnees=origine, prix=prix)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
