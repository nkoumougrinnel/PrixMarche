<p align="center">
  <img src="docs/logo.png" alt="Logo PrixMarché" width="110">
</p>

<h1 align="center">PrixMarché</h1>
<p align="center"><b>Prévision des prix des denrées</b> · Thème 10 · Groupe 5 · SUP'PTIC</p>

---

PrixMarché est une application Android qui aide les petits commerçants des marchés de Yaoundé
à suivre les prix des denrées et à anticiper ceux de la semaine suivante.

> ⚠️ **Données fictives.** Les prix utilisés pour entraîner et démontrer le modèle sont générés
> par un script (`data/`). Ils sont signalés comme tels dans les fichiers, l'API et l'application.
> Seuls les relevés faits au marché Mokolo pendant le projet sont réels (`source = reel`).

## Fonctionnalités (première version)

| Fonctionnalité | Description |
|---|---|
| Consultation des prix | Prix de 8 produits dans 3 marchés : Mokolo, Mfoundi, Essos |
| Saisie des prix | Produit, marché, prix, unité, date — possible sans connexion |
| Graphiques | Historique, prévision et seuils (MPAndroidChart) |
| Prévision | Prix estimé pour la semaine suivante, avec son erreur moyenne (MAE) |
| Alertes | Notification quand un prix franchit un seuil fixé, à la hausse ou à la baisse |

Hors périmètre : comptes utilisateurs, carte, prévisions sur plusieurs semaines.

## Organisation du dépôt

```
android/   Application Android (Java/XML, SQLite, Retrofit, MPAndroidChart)
api/       API Flask (pandas, scikit-learn, joblib) : /products, /prices, /predict
data/      Générateur de données fictives, relevés réels, entraînement du modèle
docs/      Présentation du projet, maquette, rapport
```

Chaque dossier a son propre `README.md`. Le **format commun des données** est défini dans
[`data/README.md`](data/README.md) : tout le monde l'utilise, sans exception.

## Équipe

| Membre | GitHub | Responsabilités |
|---|---|---|
| Germain | [@nkoumougrinnel](https://github.com/nkoumougrinnel) | Coordination, dépôt, API Flask, rapport final |
| Dylan | [@CRACK-CODE007](https://github.com/CRACK-CODE007) | Données fictives, entraînement et comparaison des modèles |
| Bachirou | [@BachirouOumarou](https://github.com/BachirouOumarou) | Écrans Android, graphiques, SQLite, Retrofit |
| Yaya | [@yayaboubakary245-afk](https://github.com/yayaboubakary245-afk) | Alertes et notifications, hors ligne, relevés terrain, tests |

## Planning (9 au 22 novembre 2026)

Le planning est suivi dans les **jalons** (onglet *Issues → Milestones*).

| Jalon | Période | Contenu |
|---|---|---|
| 1. Démarrage | 9 – 12 nov. | Dépôt, générateur v1, squelette API, premier écran Android |
| 2. Modèles et fonctionnalités | 13 – 16 nov. | Modèles, `/predict`, graphiques, alertes |
| 3. Intégration et hors ligne | 17 – 19 nov. | Connexion des parties, fonctionnement hors ligne |
| 4. Tests, APK et rapport | 20 – 22 nov. | Tests, corrections, APK, rapport |

Des relevés de prix sont faits chaque semaine au marché Mokolo.

## Comment travailler

Lire [`CONTRIBUTING.md`](CONTRIBUTING.md) avant le premier commit. En résumé :

1. Prendre une issue qui vous est attribuée.
2. Créer une branche depuis `main` : `feature/<numero>-<sujet>` (ex. `feature/7-generateur`).
3. Ouvrir une pull request qui contient `Closes #<numero>`, relue par un autre membre.

## Documents

- [Présentation du projet (enseignant)](docs/Presentation_Groupe5_PrixMarche.pdf)
- [Présentation détaillée pour l'équipe](docs/presentation_equipe.pdf)
- [Maquette des écrans](docs/maquette/)

## Livrables

APK · API Flask · générateur de données · rapport.
