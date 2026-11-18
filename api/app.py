"""API Flask de PrixMarché."""

import os
from pathlib import Path

import pandas as pd
from flask import Flask, jsonify

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


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
