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


if __name__ == "__main__":
    unittest.main()