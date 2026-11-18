# api/ — API Flask

Responsable : Germain ([@nkoumougrinnel](https://github.com/nkoumougrinnel))

L'API sert les prix et les prévisions à l'application Android, et reçoit les relevés saisis sur le téléphone.

## Lancer en local

```bash
cd api
python -m venv .venv
source .venv/bin/activate        # Windows : .venv\Scripts\activate
pip install -r requirements.txt
flask --app app run --host 0.0.0.0 --port 5000
```

Vérification : `http://localhost:5000/health` doit répondre `{"statut": "ok"}`.
Depuis le téléphone, utiliser l'adresse IP de l'ordinateur sur le même réseau (ex. `http://192.168.1.20:5000`).

## Routes prévues

Toutes les réponses sont en JSON et indiquent `"donnees": "fictives"` quand les prix viennent du générateur.

| Méthode | Route | Rôle |
|---|---|---|
| `GET` | `/health` | Vérifier que l'API répond |
| `GET` | `/products` | Liste des produits et des marchés |
| `GET` | `/prices?produit=tomate&marche=mokolo&depuis=2026-10-26` | Historique trié des prix ; `depuis` (facultatif) filtre à partir d'une date incluse |
| `POST` | `/prices` | Enregistrer un ou plusieurs relevés envoyés par le téléphone |
| `GET` | `/predict?produit=tomate&marche=mokolo` | Prévision pour la semaine suivante |

### Exemple de réponse `/prices`

```json
{
  "produit": "tomate",
  "marche": "mokolo",
  "donnees": "fictives",
  "prix": [
    {"semaine": "2026-09-21", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 605, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-09-28", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 622, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-10-05", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 639, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-10-12", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 656, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-10-19", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 673, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-10-26", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 690, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-11-02", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 616, "unite": "kg", "source": "synthetique"},
    {"semaine": "2026-11-09", "produit": "tomate", "marche": "mokolo", "prix_fcfa": 633, "unite": "kg", "source": "synthetique"}
  ]
}
```

Les paramètres `produit` et `marche` sont obligatoires. `depuis` accepte une date `AAAA-MM-JJ` et inclut cette semaine.

### Exemple de réponse `/predict`

```json
{
  "produit": "tomate",
  "marche": "mokolo",
  "semaine_cible": "2026-11-16",
  "prix_prevu": 720,
  "mae": 45,
  "tendance": "hausse",
  "modele": "foret_aleatoire",
  "donnees": "fictives"
}
```

`tendance` vaut `hausse`, `baisse` ou `stable` (écart de moins de 2 % avec le dernier prix connu).

### Doublons

Un relevé est identifié par (`semaine`, `produit`, `marche`, `auteur`). Un même relevé envoyé deux fois
(par exemple après une coupure réseau) n'est enregistré qu'une seule fois.
