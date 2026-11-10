# data/ — Données et modèle

Responsable : Dylan ([@CRACK-CODE007](https://github.com/CRACK-CODE007)) · Relevés réels : Yaya

```
data/
├── generateur/     Script de génération des données fictives
├── releves/        Relevés réels faits au marché Mokolo
├── entrainement/   Préparation des variables, entraînement et comparaison des modèles
└── modeles/        Modèle retenu, exporté avec joblib (utilisé par l'API)
```

## Format commun des données

Chaque prix est **une ligne** d'un fichier CSV (séparateur `,`, encodage UTF-8), avec exactement ces colonnes, dans cet ordre.
Le même format sert dans le générateur, l'API et la base SQLite du téléphone.

| Colonne | Type | Signification | Exemple |
|---|---|---|---|
| `semaine` | date `AAAA-MM-JJ` | Lundi de la semaine du relevé | `2026-11-09` |
| `produit` | texte | Identifiant du produit (liste ci-dessous) | `tomate` |
| `marche` | texte | Identifiant du marché (liste ci-dessous) | `mokolo` |
| `prix_fcfa` | entier | Prix moyen de la semaine, en FCFA | `650` |
| `unite` | texte | Unité de vente | `kg` |
| `source` | texte | `synthetique` ou `reel` | `synthetique` |

```csv
semaine,produit,marche,prix_fcfa,unite,source
2026-11-02,tomate,mokolo,650,kg,synthetique
2026-11-09,tomate,mokolo,700,kg,reel
```

### Produits

| Identifiant | Nom affiché | Unité |
|---|---|---|
| `tomate` | Tomate | `kg` |
| `plantain` | Plantain | `regime` |
| `riz` | Riz | `kg` |
| `huile_palme` | Huile de palme | `litre` |
| `oignon` | Oignon | `kg` |
| `arachide` | Arachide | `kg` |
| `macabo` | Macabo | `kg` |
| `poisson` | Poisson (maquereau) | `kg` |

### Marchés

| Identifiant | Nom affiché |
|---|---|
| `mokolo` | Mokolo |
| `mfoundi` | Mfoundi |
| `essos` | Essos |

## Données fictives attendues

- 3 ans de relevés hebdomadaires, 8 produits, 3 marchés.
- Chaque prix combine : prix de départ, évolution dans le temps, saisons des pluies,
  fêtes (Noël, Tabaski, rentrée scolaire) et variations aléatoires.
- Le script est **reproductible** : même graine aléatoire → mêmes données.

## Évaluation du modèle

- Entraînement sur les 2 premières années, test sur la 3ᵉ (découpage chronologique, jamais aléatoire).
- Méthodes comparées : prévision naïve (référence), régression linéaire, forêt aléatoire.
- Indicateurs : MAE (principal), RMSE (complément).
- Objectif : MAE au moins 15 % plus faible que celle de la prévision naïve.
