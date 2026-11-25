package cm.supptic.prixmarche;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prixmarche.db";
    private static final int DATABASE_VERSION = 1;

    // Table des produits
    public static final String TABLE_PRODUIT = "produit";
    public static final String COL_PRODUIT_ID = "id";
    public static final String COL_PRODUIT_NOM = "nom";
    public static final String COL_PRODUIT_UNITE = "unite";

    // Table des prix (historique et saisies)
    public static final String TABLE_PRIX = "prix_historique";
    public static final String COL_PRIX_ID = "id";
    public static final String COL_PRIX_PRODUIT_ID = "produit_id";
    public static final String COL_PRIX_MARCHE = "marche"; // ex: Mokolo, Mfoundi, Essos
    public static final String COL_PRIX_VALEUR = "valeur";
    public static final String COL_PRIX_DATE = "date";
    public static final String COL_PRIX_SOURCE = "source"; // "reel" ou "fictif"

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de la table produit
        String createTableProduit = "CREATE TABLE " + TABLE_PRODUIT + " (" +
                COL_PRODUIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUIT_NOM + " TEXT NOT NULL, " +
                COL_PRODUIT_UNITE + " TEXT NOT NULL);";

        // Création de la table prix_historique
        String createTablePrix = "CREATE TABLE " + TABLE_PRIX + " (" +
                COL_PRIX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRIX_PRODUIT_ID + " INTEGER, " +
                COL_PRIX_MARCHE + " TEXT NOT NULL, " +
                COL_PRIX_VALEUR + " REAL NOT NULL, " +
                COL_PRIX_DATE + " TEXT NOT NULL, " +
                COL_PRIX_SOURCE + " TEXT DEFAULT 'reel', " +
                "FOREIGN KEY(" + COL_PRIX_PRODUIT_ID + ") REFERENCES " + TABLE_PRODUIT + "(" + COL_PRODUIT_ID + "));";

        db.execSQL(createTableProduit);
        db.execSQL(createTablePrix);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUIT);
        onCreate(db);
    }
// --- MÉTHODES POUR LES PRODUITS ---

    // Insérer un produit
    public boolean insererProduit(String nom, String unite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUIT_NOM, nom);
        values.put(COL_PRODUIT_UNITE, unite);
        long result = db.insert(TABLE_PRODUIT, null, values);
        return result != -1; // Retourne true si l'insertion a réussi
    }

    // Récupérer tous les produits sous forme de Curseurs ou liste
    public Cursor obtenirTousLesProduits() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUIT, null);
    }

    // --- MÉTHODES POUR LES PRIX ---

    // Insérer un relevé de prix (pour le mode hors-ligne / saisie manuelle)
    public boolean insererPrix(int produitId, String marche, double valeur, String date, String source) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRIX_PRODUIT_ID, produitId);
        values.put(COL_PRIX_MARCHE, marche);
        values.put(COL_PRIX_VALEUR, valeur);
        values.put(COL_PRIX_DATE, date);
        values.put(COL_PRIX_SOURCE, source);
        long result = db.insert(TABLE_PRIX, null, values);
        return result != -1;
    }

    // Récupérer les prix pour un marché spécifique (ex: "Mokolo", "Mfoundi", "Essos")
    public Cursor obtenirPrixParMarche(String marche) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT p." + COL_PRODUIT_NOM + ", pr." + COL_PRIX_VALEUR +
                ", pr." + COL_PRIX_DATE + ", pr." + COL_PRIX_SOURCE +
                " FROM " + TABLE_PRIX + " pr INNER JOIN " + TABLE_PRODUIT +
                " p ON pr." + COL_PRIX_PRODUIT_ID + " = p." + COL_PRODUIT_ID +
                " WHERE pr." + COL_PRIX_MARCHE + " = ?", new String[]{marche});
    }
    // Récupérer tous les relevés de prix de tous les marchés avec le nom du produit
    public Cursor obtenirTousLesPrix() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p." + COL_PRODUIT_NOM + ", "
                + "pr." + COL_PRIX_MARCHE + ", "
                + "pr." + COL_PRIX_VALEUR + ", "
                + "pr." + COL_PRIX_DATE + ", "
                + "pr." + COL_PRIX_SOURCE + " "
                + "FROM " + TABLE_PRIX + " pr "
                + "INNER JOIN " + TABLE_PRODUIT + " p "
                + "ON pr." + COL_PRIX_PRODUIT_ID + " = p." + COL_PRODUIT_ID + " "
                + "ORDER BY pr." + COL_PRIX_ID + " DESC";
        return db.rawQuery(query, null);
    }
}