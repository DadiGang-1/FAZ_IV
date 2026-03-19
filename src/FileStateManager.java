import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

/**
 * Gère la lecture et l'écriture du fichier currentFile.txt
 * qui permet de tracker le dernier lot traité
 */
public class FileStateManager {
    
    private String filePath;

    public FileStateManager(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Récupère le numéro du lot actuellement traité
     * Si le fichier n'existe pas, le crée et retourne 0
     */
    public int getCurrentLot() {
        int lotFind = 0;
        File file = new File(filePath);
        
        if (!file.exists()) {
            createFile(file);
        } else {
            try {
                Scanner fileScanner = new Scanner(new FileReader(filePath));
                if (fileScanner.hasNextLine()) {
                    lotFind = Integer.parseInt(fileScanner.nextLine());
                }
                fileScanner.close();
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }
        return lotFind;
    }

    /**
     * Écrit le numéro du lot actuellement traité dans le fichier
     */
    public void writeCurrentLot(String lotNumber) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(filePath))) {
            printWriter.print(lotNumber.split(".csv")[0]);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Crée le fichier et son répertoire parent si nécessaire
     */
    private void createFile(File file) {
        String parentDirStr = file.getParent();
        File parentDir = new File(parentDirStr);
        
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                System.err.println("The parent directory could not be created");
                return;
            }
        }
        
        try {
            file.createNewFile();
            System.out.println("File successfully created: " + filePath);
        } catch (IOException ioe) {
            System.err.println("Unable to create file. " + ioe.getMessage());
        }
    }
}
