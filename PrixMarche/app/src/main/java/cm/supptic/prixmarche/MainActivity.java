package cm.supptic.prixmarche;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etNomProduit;
    private EditText etNomMarche;
    private EditText etPrixProduit;
    private Button btnEnregistrer;
    private Button btnAfficher;
    private Button btnFiltrerMarche;
    private TextView tvResultats;
    private TextView tvCompteurReleves;
    private PrixChartView prixChartView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etNomProduit = findViewById(R.id.etNomProduit);
        etNomMarche = findViewById(R.id.etNomMarche);
        etPrixProduit = findViewById(R.id.etPrixProduit);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);
        btnAfficher = findViewById(R.id.btnAfficher);
        btnFiltrerMarche = findViewById(R.id.btnFiltrerMarche);
        tvResultats = findViewById(R.id.tvResultats);
        tvCompteurReleves = findViewById(R.id.tvCompteurReleves);
        prixChartView = findViewById(R.id.prixChartView);

        btnEnregistrer.setOnClickListener(v -> enregistrerDonnees());

        btnAfficher.setOnClickListener(v -> afficherTousLesReleves(true));

        btnFiltrerMarche.setOnClickListener(v -> {
            String marche = etNomMarche.getText().toString().trim();
            if (marche.isEmpty()) {
                Toast.makeText(this, "Saisissez un marché pour filtrer.", Toast.LENGTH_SHORT).show();
                return;
            }
            afficherRelevesParMarche(marche);
        });

        // Chargement initial au démarrage
        afficherTousLesReleves(false);
    }

    private void enregistrerDonnees() {
        String produit = etNomProduit.getText().toString().trim();
        String marche = etNomMarche.getText().toString().trim();
        String prixTexte = etPrixProduit.getText().toString().trim();

        if (produit.isEmpty() || marche.isEmpty() || prixTexte.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        double valeurPrix;
        try {
            valeurPrix = Double.parseDouble(prixTexte);
            if (valeurPrix <= 0) {
                Toast.makeText(this, "Le prix doit être supérieur à 0.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez saisir un prix valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        int produitId = trouverProduitId(produit);
        if (produitId == -1) {
            boolean produitInsere = dbHelper.insererProduit(produit, "unité");
            if (!produitInsere) {
                Toast.makeText(this, "Erreur lors de l'ajout du produit.", Toast.LENGTH_SHORT).show();
                return;
            }
            produitId = trouverProduitId(produit);
        }

        if (produitId == -1) {
            Toast.makeText(this, "Impossible de récupérer l'identifiant du produit.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateActuelle = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String source = "manuel";

        boolean prixInsere = dbHelper.insererPrix(produitId, marche, valeurPrix, dateActuelle, source);
        if (prixInsere) {
            Toast.makeText(this, "Relevé enregistré avec succès.", Toast.LENGTH_SHORT).show();
            etNomProduit.setText("");
            etPrixProduit.setText("");
            afficherTousLesReleves(false);
        } else {
            Toast.makeText(this, "Échec de l'enregistrement du prix.", Toast.LENGTH_SHORT).show();
        }
    }

    private int trouverProduitId(String nomProduit) {
        int idTrouve = -1;
        Cursor cursor = dbHelper.obtenirTousLesProduits();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getColumnCount() >= 2) {
                            String nom = cursor.getString(1);
                            if (nom != null && nom.equalsIgnoreCase(nomProduit)) {
                                idTrouve = cursor.getInt(0);
                                break;
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return idTrouve;
    }

    private void afficherTousLesReleves(boolean afficherFeedback) {
        Cursor cursor = dbHelper.obtenirTousLesPrix();
        afficherDepuisCursor(cursor, getString(R.string.tv_aucun_resultat));
        if (afficherFeedback) {
            Toast.makeText(this, "Données actualisées.", Toast.LENGTH_SHORT).show();
        }
    }

    private void afficherRelevesParMarche(String marche) {
        Cursor cursor = dbHelper.obtenirPrixParMarche(marche);
        afficherDepuisCursor(cursor, getString(R.string.tv_aucun_resultat_marche));
        Toast.makeText(this, "Filtre appliqué : " + marche, Toast.LENGTH_SHORT).show();
    }

    private void afficherDepuisCursor(Cursor cursor, String messageVide) {
        StringBuilder builder = new StringBuilder();
        List<PrixChartView.PointPrix> pointsGraphique = new ArrayList<>();
        int compteur = 0;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        compteur++;
                        // Colonnes attendues : 0=nom_produit, 1=marche, 2=valeur, 3=date, 4=source
                        String nomProduit = cursor.getColumnCount() > 0 ? cursor.getString(0) : "";
                        String marche = cursor.getColumnCount() > 1 ? cursor.getString(1) : "";
                        String prixStr = cursor.getColumnCount() > 2 ? cursor.getString(2) : "0";
                        String dateStr = cursor.getColumnCount() > 3 ? cursor.getString(3) : "";
                        String sourceStr = cursor.getColumnCount() > 4 ? cursor.getString(4) : "";

                        double prixNum = 0.0;
                        try {
                            prixNum = Double.parseDouble(prixStr);
                        } catch (NumberFormatException ignored) {
                        }

                        pointsGraphique.add(new PrixChartView.PointPrix(nomProduit, prixNum));

                        builder.append("• ").append(nomProduit)
                                .append(" — ").append(prixStr).append(" FCFA\n")
                                .append("   Marché : ").append(marche)
                                .append(" | Date : ").append(dateStr)
                                .append(" | Source : ").append(sourceStr);

                        if (compteur < cursor.getCount()) {
                            builder.append("\n\n");
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        tvCompteurReleves.setText(getString(R.string.label_total_releves, compteur));
        prixChartView.setDonnees(pointsGraphique);

        if (builder.length() == 0) {
            tvResultats.setText(messageVide);
        } else {
            tvResultats.setText(builder.toString());
        }
    }
}