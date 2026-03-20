import java.util.HashMap;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Écrit les fichiers .ba2 à partir des données de CommandeRepereCase
 */
public class Ba2FileWriter {
    
    private String outputDirectory;

    public Ba2FileWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Écrit les données dans un fichier .ba2
     */
    public void writeFile(HashMap<String, CommandeRepereCase> listeCaseRepere, int lotNumber) {
        String filePath = outputDirectory + lotNumber + ".ba2";
        
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(filePath))) {
            for (String cr : listeCaseRepere.keySet()) {
                printWriter.println(CommandeRepereCase.writeToFile(listeCaseRepere.get(cr)));
            }
            System.out.println("File written successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
