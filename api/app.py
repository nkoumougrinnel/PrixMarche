"""API Flask de PrixMarché.

Squelette de départ : seule la route /health est en place.
Les routes /products, /prices et /predict sont suivies dans les issues du dépôt.
"""

from flask import Flask, jsonify

app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify(statut="ok")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
