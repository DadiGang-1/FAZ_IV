//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardWatchEventKinds;
//import java.nio.file.WatchEvent;
//import java.nio.file.WatchKey;
//import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Point d'entrée principal de l'application FAZ IV
 * Orchestre l'exécution des services de lecture et écriture de fichiers
 */
public class Faz {

    private FileStateManager fileStateManager;
    private Ba2FileWriter ba2FileWriter;
    
    private String sourcePath = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Source\\";
    private String currentFilePath = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Data\\currentFile.txt";
    private String outputDirectory = "../test/";

    public Faz() {
        this.fileStateManager = new FileStateManager(currentFilePath);
        this.ba2FileWriter = new Ba2FileWriter(outputDirectory);
    }

    /**
     * Lance le traitement de tous les fichiers non traités
     */
    public void processFiles() {
        // Récupère le dernier lot traité
        int currentLot = fileStateManager.getCurrentLot();
        
        // Récupère la liste des fichiers non traités
        ArrayList<String> fileNames = CsvDataReader.getUnprocessedFiles(sourcePath, currentLot);

        // Traite chaque fichier
        for (String fileName : fileNames) {
            System.out.println("Processing: " + sourcePath + fileName);
            
            // Parse le fichier CSV
            HashMap<String, CommandeRepereCase> listeCaseRepere = CsvDataReader.parseFile(sourcePath + fileName);
            
            // Extrait le numéro de lot depuis les données
            int lotNumber = extractLotNumber(listeCaseRepere);
            
            // Écrit le fichier .ba2
            ba2FileWriter.writeFile(listeCaseRepere, lotNumber);
            
            // Met à jour le fichier d'état
            fileStateManager.writeCurrentLot(fileName);
        }
    }

    /**
     * Extrait le numéro de lot depuis les données de CommandeRepereCase
     */
    private int extractLotNumber(HashMap<String, CommandeRepereCase> listeCaseRepere) {
        if (listeCaseRepere.isEmpty()) {
            return 0;
        }
        return listeCaseRepere.values().iterator().next().getLot();
    }

    public static void main(String[] args) {
        Faz faz = new Faz();
        faz.processFiles();

        // TODO:
        // Créer un service de surveillance en continue pour traité tous les fichiers à venir
        // Watch dir 
        //try {
        //    WatchService watchService = FileSystems.getDefault().newWatchService();
        //    Path path = Paths.get(sourcePath);
        //    path.register(
        //        watchService,
        //        StandardWatchEventKinds.ENTRY_CREATE,
        //        StandardWatchEventKinds.ENTRY_DELETE,
        //        StandardWatchEventKinds.ENTRY_MODIFY
        //    );
        //    System.out.println("Surveillance du dossier : " + path);
        //
        //    while (true) {
        //        WatchKey key = watchService.take(); // bloque jusqu'à un événement
        //
        //        for (WatchEvent<?> event : key.pollEvents()) {
        //            WatchEvent.Kind<?> kind = event.kind();
        //            Path fileName = (Path) event.context();
        //            System.out.println("Event : " + kind + " sur " + fileName);
        //            // processFiles 
        //            faz.processFiles();
        //        }
        //        boolean valid = key.reset();
        //        if (!valid) {
        //            break;
        //        }
        //    }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
}