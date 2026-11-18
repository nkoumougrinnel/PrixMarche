import unittest

from api.app import app


class ApiRoutesTestCase(unittest.TestCase):
    def setUp(self):
        self.client = app.test_client()

    def test_health_returns_ok(self):
        response = self.client.get("/health")

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.get_json(), {"statut": "ok"})

    def test_products_returns_catalogue_and_data_origin(self):
        response = self.client.get("/products")

        self.assertEqual(response.status_code, 200)
        self.assertEqual(
            response.get_json(),
            {
                "produits": [
                    {"id": "tomate", "nom": "Tomate", "unite": "kg"},
                    {"id": "plantain", "nom": "Plantain", "unite": "regime"},
                    {"id": "riz", "nom": "Riz", "unite": "kg"},
                    {"id": "huile_palme", "nom": "Huile de palme", "unite": "litre"},
                    {"id": "oignon", "nom": "Oignon", "unite": "kg"},
                    {"id": "arachide", "nom": "Arachide", "unite": "kg"},
                    {"id": "macabo", "nom": "Macabo", "unite": "kg"},
                    {"id": "poisson", "nom": "Poisson (maquereau)", "unite": "kg"},
                ],
                "marches": [
                    {"id": "mokolo", "nom": "Mokolo"},
                    {"id": "mfoundi", "nom": "Mfoundi"},
                    {"id": "essos", "nom": "Essos"},
                ],
                "donnees": "fictives",
            },
        )

    def test_prices_returns_matching_rows(self):
        response = self.client.get("/prices?produit=tomate&marche=mokolo")

        self.assertEqual(response.status_code, 200)
        payload = response.get_json()
        self.assertEqual(payload["produit"], "tomate")
        self.assertEqual(payload["marche"], "mokolo")
        self.assertEqual(payload["donnees"], "fictives")
        self.assertEqual(len(payload["prix"]), 8)
        self.assertTrue(all(row["produit"] == "tomate" for row in payload["prix"]))
        self.assertTrue(all(row["marche"] == "mokolo" for row in payload["prix"]))
        self.assertTrue(all(isinstance(row["prix_fcfa"], int) for row in payload["prix"]))

    def test_prices_are_sorted_by_week(self):
        response = self.client.get("/prices?produit=tomate&marche=mokolo")

        weeks = [row["semaine"] for row in response.get_json()["prix"]]
        self.assertEqual(weeks, sorted(weeks))

    def test_prices_filters_from_date_inclusive(self):
        response = self.client.get(
            "/prices?produit=tomate&marche=mokolo&depuis=2026-10-26"
        )

        self.assertEqual(response.status_code, 200)
        rows = response.get_json()["prix"]
        self.assertEqual(len(rows), 3)
        self.assertTrue(all(row["semaine"] >= "2026-10-26" for row in rows))

    def test_prices_returns_400_when_required_parameter_is_missing(self):
        for query in (
            "/prices?marche=mokolo",
            "/prices?produit=tomate",
        ):
            with self.subTest(query=query):
                response = self.client.get(query)

                self.assertEqual(response.status_code, 400)
                self.assertIn("erreur", response.get_json())

    def test_prices_returns_400_for_unknown_product_or_market(self):
        for query in (
            "/prices?produit=banane&marche=mokolo",
            "/prices?produit=tomate&marche=inconnu",
        ):
            with self.subTest(query=query):
                response = self.client.get(query)

                self.assertEqual(response.status_code, 400)
                self.assertIn("erreur", response.get_json())

    def test_prices_returns_400_for_invalid_since_date(self):
        for since in ("2026-02-30", ""):
            with self.subTest(since=since):
                response = self.client.get(
                    f"/prices?produit=tomate&marche=mokolo&depuis={since}"
                )

                self.assertEqual(response.status_code, 400)
                self.assertIn("erreur", response.get_json())


if __name__ == "__main__":
    unittest.main()