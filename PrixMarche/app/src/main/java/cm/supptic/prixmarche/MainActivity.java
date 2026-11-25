package cm.supptic.prixmarche;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etNomProduit;
    private EditText etNomMarche;
    private EditText etPrixProduit;
    private Button btnEnregistrer;
    private Button btnAfficher;
    private TextView tvResultats;
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
        tvResultats = findViewById(R.id.tvResultats);

        btnEnregistrer.setOnClickListener(v -> enregistrerDonnees());
        btnAfficher.setOnClickListener(v -> afficherDonnees());
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
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez saisir un prix valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniteDefaut = "unité";
        dbHelper.insererProduit(produit, uniteDefaut);

        int produitId = trouverProduitId(produit);
        if (produitId == -1) {
            Toast.makeText(this, "Impossible de récupérer l'identifiant du produit.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateActuelle = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String source = "manuel";

        boolean prixInsere = dbHelper.insererPrix(produitId, marche, valeurPrix, dateActuelle, source);
        if (prixInsere) {
            Toast.makeText(this, "Enregistrement effectué.", Toast.LENGTH_SHORT).show();
            etNomProduit.setText("");
            etPrixProduit.setText("");
            afficherDonnees();
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
                        // Suppose généralement colonne 0 = id, colonne 1 = nom
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

    private void afficherDonnees() {
        String marcheFiltre = etNomMarche.getText().toString().trim();
        StringBuilder builder = new StringBuilder();

        if (!marcheFiltre.isEmpty()) {
            Cursor cursor = dbHelper.obtenirPrixParMarche(marcheFiltre);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            StringBuilder ligne = new StringBuilder();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                if (i > 0) {
                                    ligne.append(" | ");
                                }
                                ligne.append(cursor.getString(i));
                            }
                            builder.append(ligne).append("\n");
                        } while (cursor.moveToNext());
                    }
                } finally {
                    cursor.close();
                }
            }
        } else {
            Cursor cursor = dbHelper.obtenirTousLesProduits();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            StringBuilder ligne = new StringBuilder();
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                if (i > 0) {
                                    ligne.append(" | ");
                                }
                                ligne.append(cursor.getString(i));
                            }
                            builder.append(ligne).append("\n");
                        } while (cursor.moveToNext());
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        if (builder.length() == 0) {
            tvResultats.setText(R.string.tv_aucun_resultat);
        } else {
            tvResultats.setText(builder.toString().trim());
        }
    }
}