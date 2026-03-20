import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Lit et parse les fichiers CSV pour extraire les cas de commande
 */
public class CsvDataReader {
    
    /**
     * Récupère la liste des fichiers CSV non encore traités
     * Retourne les fichiers dont le numéro est supérieur au currentLot
     */
    public static ArrayList<String> getUnprocessedFiles(String directory, int currentLot) {
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            File dir = new File(directory);
            String[] allFileNames = dir.list();
            
            if (allFileNames == null || allFileNames.length == 0) {
                System.out.println("The directory is empty or does not exist: " + directory);
                return fileNames;
            }
            
            for (String fileName : allFileNames) {
                try {
                    int fileNumber = Integer.parseInt(fileName.split(".csv")[0]);
                    if (fileNumber > currentLot) {
                        fileNames.add(fileName);
                    }
                } catch (NumberFormatException e) {
                    // Ignorer les fichiers qui ne commencent pas par un nombre
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting unprocessed files: " + e.getMessage());
        }
        return fileNames;
    }

    /**
     * Parse un fichier CSV et retourne une HashMap de CommandeRepereCase
     */
    public static HashMap<String, CommandeRepereCase> parseFile(String filePath) {
        HashMap<String, CommandeRepereCase> listeCaseRepere = new HashMap<>();
        
        try {
            FileReader fileReader = new FileReader(filePath);
            Scanner fileScanner = new Scanner(fileReader);
            
            String profil = "";
            int repere = 0;
            int quantite = 0;
            int lot = 0;
            String commande = "";
            boolean inRepere = false;
            boolean inProfil = false;
            String idCRC = "";

            while (fileScanner.hasNext()) {
                String fileLine = fileScanner.nextLine();
                
                // Récupération du numéro de lot et du numéro de commande
                if (fileLine.contains("LOT")) {
                    lot = Integer.parseInt(fileLine.split(";")[1]);
                    commande = fileLine.split(";")[3];
                    inRepere = false;
                    inProfil = false;
                }

                // Récupération du numéro de repère et de la quantité d'un repère
                if (fileLine.contains("Rep")) {
                    inRepere = true;
                    inProfil = false;
                    String texteRepere = fileLine.split(";")[0];
                    String texteQuantite = fileLine.split(";")[1];
                    repere = Integer.parseInt(texteRepere.split("re")[1]);
                    quantite = Integer.parseInt(texteQuantite.split(" : ")[1]);
                    continue;
                }
                
                // Récupération du profil d'un repère
                if (fileLine.contains("PROFIL")) {
                    inProfil = true;
                    inRepere = false;
                    profil = fileLine.substring(8).replace(" ", "");
                    
                    for (String key : listeCaseRepere.keySet()) {
                        if (key.contains(commande + "-" + repere)) {
                            listeCaseRepere.get(key).setProfil(profil);
                        }
                    }
                    continue;
                }
                
                // Récupération des dimensions d'une case d'un repère
                if (inRepere) {
                    int caseNumber = Integer.parseInt(fileLine.split(";")[0].trim());
                    String dimension = fileLine.split(";")[1].replace(" * ", "x").replace(",", ".");
                    double caseLargeur = Double.parseDouble(dimension.split("x")[0]);
                    double caseHauteur = Double.parseDouble(dimension.split("x")[1]);
                    double caseHauteurPoignee = 0.0;
                    
                    try {
                        caseHauteurPoignee = Double.parseDouble(fileLine.split(";")[2].replace(",", "."));
                    } catch (Exception e) {
                        // Valeur par défaut si non disponible
                    }
                    
                    idCRC = commande + "-" + repere + "-" + caseNumber;
                    listeCaseRepere.put(idCRC, 
                        new CommandeRepereCase(repere, caseNumber, caseLargeur, caseHauteur, 
                            caseHauteurPoignee, lot, commande, profil));
                }

                // Récupération de tous les détails d'un repère
                if (inProfil) {
                    int OT = Integer.parseInt(fileLine.split(";")[1]);
                    String findLine = commande + "-" + repere + "-" + OT;
                    CommandeRepereCase crc = listeCaseRepere.get(findLine);
                    
                    if (crc != null) {
                        addDetailToCase(crc, fileLine, quantite);
                    }
                }
            }
            fileScanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing CSV file: " + e.getMessage());
        }
        
        return listeCaseRepere;
    }

    /**
     * Ajoute un détail à une case de CommandeRepereCase
     */
    private static void addDetailToCase(CommandeRepereCase crc, String fileLine, int quantite) {
        try {
            char position = fileLine.split(";")[3].charAt(0);
            String code = fileLine.split(";")[5];
            String codeFerrureEnum = "_" + code.replace("-", "_");
            int quantiteCode = Integer.parseInt(fileLine.split(";")[7]);
            if (quantite != 0) quantiteCode /= quantite;
            Double coupe = Double.parseDouble(fileLine.split(";")[9].replace(",", "."));
            
            // Exception sur la génération des fichiers
            if (code.equals("222214")) position = 'H';
            
            try {
                Ferrure ferrure = Ferrure.valueOf(codeFerrureEnum);
                String designation = ferrure.getDesignation();
                TypeFerrure typeFerrure = ferrure.getType();
                ArrayList<Double> trouDeVisList = new ArrayList<>();
                
                // Calcul des trous de vis selon le type de ferrure
                for (Double trouDeVis : ferrure.getTrouDeVis()) {
                    if(ferrure.getType() != TypeFerrure.SERRURE || ferrure.getType() != TypeFerrure.VERROU || ferrure.getType() != TypeFerrure.CREMONE_SOUFFLET) {
                        if(crc.getHauteurPoignee() != 0.0) { // crc.hauteurPoignee
                            //System.out.println("HauteurPoignée : "+crc.getHauteurPoignee());
                            // TODO:
                            // Gérer les trou de vis en fonction de la hauteur poignee ou en fonction de la dimension du chassis
                        }
                    }

                    if (trouDeVis < coupe || coupe <= 0.0) {
                        trouDeVisList.add(trouDeVis);
                    } else {
                        if (ferrure.getType() != TypeFerrure.RAB && ferrure.getType() != TypeFerrure.RAH) {
                            if (ferrure.getType() != TypeFerrure.STB) {
                                Double newHole = coupe - 8.0;
                                trouDeVisList.add(newHole);
                            } else {
                                Double newHole = coupe + 8.0;
                                trouDeVisList.add(newHole);
                            }
                            break;
                        }
                    }
                }
                
                // Ajouter les détails
                for (int i = 0; i < quantiteCode; i++) {
                    Detail detail = new Detail(position, code, coupe, trouDeVisList, designation, typeFerrure);
                    crc.addDetails(detail);
                }
                
            } catch (Exception e) {
                System.err.println("Unknown ferrure code: " + code);
            }
        } catch (Exception e) {
            System.err.println("Error adding detail: " + e.getMessage());
        }
    }
}
