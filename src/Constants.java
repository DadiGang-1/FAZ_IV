/**
 * Classe centralisée pour toutes les constantes et chemins du projet.
 * À adapter selon votre environnement d'exécution.
 */
public class Constants {
    
    // ==================== CHEMINS ====================
    
    /** Répertoire source contenant les fichiers CSV */
    public static final String SOURCE_PATH = 
        "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Source\\";
    
    /** Fichier de suivi du lot courant traité */
    public static final String CURRENT_FILE_PATH = 
        "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Data\\currentFile.txt";
    
    /** Répertoire de sortie pour les fichiers générés */
    public static final String OUTPUT_PATH = 
        "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Code\\FAZ_IV\\test\\";
    
    /** Répertoire des classes compilées */
    public static final String CLASSES_PATH = 
        "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Code\\FAZ_IV\\classes\\";
    
    
    // ==================== EXTENSIONS ====================
    
    /** Extension des fichiers source */
    public static final String CSV_EXTENSION = ".csv";
    
    /** Extension des fichiers de sortie */
    public static final String OUTPUT_EXTENSION = ".ba2";
    
    
    // ==================== DÉLIMITEURS ====================
    
    /** Délimiteur CSV */
    public static final String CSV_DELIMITER = ";";
    
    /** Caractère astérisque */
    public static final String ASTERISK = " * ";
    
    /** Caractère virgule */
    public static final String COMMA = ",";
    
    /** Caractère point */
    public static final String DOT = ".";
    
    
    // ==================== MOTIFS DE RECHERCHE ====================
    
    /** Motif pour les lignes de lot */
    public static final String LOT_PATTERN = "LOT";
    
    /** Motif pour les lignes de repère */
    public static final String REPERE_PATTERN = "Rep";
    
    /** Motif pour les lignes profil */
    public static final String PROFIL_PATTERN = "PROFIL";
    
    
    // ==================== VALEURS PAR DÉFAUT ====================
    
    /** Lot initial par défaut */
    public static final int DEFAULT_LOT = 0;
    
    /** Commande initiale par défaut */
    public static final String DEFAULT_COMMANDE = "";
    
    /** Profil initial par défaut */
    public static final String DEFAULT_PROFIL = "";
    
    
    // ==================== CONSTANTES DE CALCUL ====================
    
    /** Offset pour calcul des trous de vis (mm) */
    public static final double CUTTING_OFFSET = 8.0;
    
    /** Offset initial pour les numéros (padding) */
    public static final String LOT_PADDING = "000000";
    
    /** Longueur finale du lot après padding */
    public static final int LOT_FORMATTED_LENGTH = 6;
    
    /** Position dans la commande à extraire */
    public static final int COMMANDE_START = 2;
    public static final int COMMANDE_END = 6;
    
    /** Padding pour le numéro de repère */
    public static final String REPERE_PADDING = "00";
    
    /** Longueur finale du repère après padding */
    public static final int REPERE_FORMATTED_LENGTH = 2;
    
    
    // ==================== FORMAT FICHIERS ====================
    
    /** Nombre de CAD générées par repère */
    public static final int CAD_COUNT = 4;
    
    /** Format de numéro CAD */
    public static final String CAD_FORMAT = "00001";
    
    
    // ==================== MESSAGES ====================
    
    public static final String DIR_EMPTY_MSG = "The directory is empty!";
    public static final String FILE_CREATED_MSG = "File successfully created!";
    public static final String PARENT_DIR_ERROR_MSG = "The parent directory could not be created";
    public static final String FILE_CREATE_ERROR_MSG = "Unable to create file.";
    public static final String FILE_READ_ERROR_MSG = "Error reading file:";
    public static final String FILE_WRITE_ERROR_MSG = "An error occurred while writing to the file:";
    public static final String DATA_EXTRACT_ERROR_MSG = "Error on data extraction:";
    public static final String GET_FILE_ERROR_MSG = "Error on getting uncomplet file:";
    public static final String FILE_NOT_FOUND_MSG = "File not found:";
    
    // Constructeur privé pour empêcher l'instanciation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
