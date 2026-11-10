# android/ — Application Android

Responsables : Bachirou ([@BachirouOumarou](https://github.com/BachirouOumarou)) pour les écrans, les graphiques, SQLite et Retrofit ·
Yaya ([@yayaboubakary245-afk](https://github.com/yayaboubakary245-afk)) pour les alertes, les notifications et le mode hors ligne.

## Choix techniques

| Élément | Choix |
|---|---|
| Langage | Java, interfaces en XML |
| Nom du paquet | `cm.supptic.prixmarche` |
| Version minimale | Android 7.0 (API 24) |
| Base locale | SQLite |
| Réseau | Retrofit + Gson |
| Graphiques | MPAndroidChart |
| Tâches en arrière-plan | WorkManager (vérification des seuils, envoi des relevés en attente) |

Le projet Android Studio sera créé dans ce dossier (issue dédiée).

## Écrans

La maquette est dans [`docs/maquette/`](../docs/maquette/) :

1. Démarrage
2. Accueil : choix du marché, prix de la semaine, tendance
3. Détail produit : courbe, prévision avec sa marge d'erreur, seuil
4. Saisie d'un prix
5. Alertes de seuil

## Base SQLite (à valider ensemble par Bachirou et Yaya)

| Table | Contenu |
|---|---|
| `prix` | Prix au format commun (voir `data/README.md`) |
| `prevision` | Dernière prévision reçue par produit et marché (pour l'affichage hors ligne) |
| `seuil` | Seuils d'alerte : produit, marché, sens (hausse ou baisse), montant, actif |
| `releve_en_attente` | Relevés saisis sans réseau, pas encore envoyés à l'API |

## Données fictives

L'application affiche clairement que les prix de démonstration sont fictifs (bandeau sur l'écran Accueil).
