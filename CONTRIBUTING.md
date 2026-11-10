# Règles de travail

Ces règles sont courtes. Elles évitent de perdre du temps à réparer le travail des autres.

## 1. Tout part d'une issue

- Une tâche = une issue, avec un responsable et un jalon.
- Si une tâche n'existe pas encore, on crée l'issue **avant** de coder.
- On commente l'issue quand on commence, quand on est bloqué et quand c'est fini.
- Un blocage de plus de deux jours se signale sur l'issue et dans le groupe.

## 2. Branches

- On ne travaille **jamais** directement sur `main`.
- Une branche par issue, créée depuis un `main` à jour :

```bash
git checkout main
git pull
git checkout -b feature/12-ecran-accueil
```

- Préfixes : `feature/` (nouvelle fonction), `fix/` (correction), `docs/` (documents, rapport).

## 3. Commits

- Petits et fréquents, un sujet par commit.
- Message au présent, en français, qui dit ce que fait le commit :

```
Ajoute l'écran Accueil avec la liste des prix
Corrige le calcul de la moyenne sur 4 semaines
```

- Ne jamais committer : mots de passe, clés, fichiers générés (`build/`, `__pycache__/`, `.venv/`).

## 4. Pull requests

- On ouvre une pull request vers `main` dès que la tâche est finie (ou en brouillon pour avoir un avis).
- La description contient `Closes #<numero>` pour fermer l'issue automatiquement.
- **Un autre membre relit** avant la fusion. Le relecteur vérifie que ça marche chez lui, pas seulement que le code « a l'air bien ».
- On fusionne avec *Squash and merge*, puis on supprime la branche.

## 5. Données

- Le format commun est dans [`data/README.md`](data/README.md). On ne le change pas sans en parler à toute l'équipe.
- Toute donnée fictive porte `source = synthetique`. Toute donnée relevée au marché porte `source = reel`.

## 6. Point d'équipe

- Un point court chaque semaine (20 minutes) : ce que j'ai fini, ce que je fais, ce qui me bloque.
- Le reste du temps, on échange dans les issues et dans le groupe de discussion.
