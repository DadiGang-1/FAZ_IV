import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.io.File;
class Faz {

    private static int lot = 0;
    private static String commande = "";
    private static String sourcePath = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Source\\";
    private static String currentFile = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Data\\currentFile.txt";
    private static int currentLot = 0;

    public static int getCurrentLot(String currentFile) {
        int lotFind = 0;
        File file = new File(currentFile);
        if (!file.exists()) {
            String parentDirStr = file.getParent();
            File parentDir = new File(parentDirStr);
            if (!parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.out.println("The parent directory could not be created");
                }
            }
            
            try {
                file.createNewFile();
                System.out.println("File successfully created!");
            } catch (IOException ioe) {
                System.out.println("Unable to create file. " + ioe.getMessage());
            }
        } else {
            try {
                Scanner fileScanner = new Scanner(new FileReader(currentFile));
                lotFind = Integer.parseInt(fileScanner.nextLine());
                fileScanner.close();
            } catch (Exception e) {
                System.out.println("Error reading file: "+e.getMessage());
            }
        }
        return lotFind;
    }
	public static ArrayList<String> getUncompletFile(File file) {
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            String AllFileNames[] = file.list();
            if (AllFileNames.length == 0) {
                System.out.println("The directory is empty!");
            } else {
                for (int i = 0; i < AllFileNames.length; i++) {
                    String uniqueFile = AllFileNames[i];
                    int uniqueNumberFile = Integer.parseInt(uniqueFile.split(".csv")[0]);
                    if(uniqueNumberFile >= currentLot) fileNames.add(uniqueFile);
                }
            }
        } catch (Exception e) {
            System.out.println("Error on getting uncomplet file: " + e.getMessage());
        }
        return fileNames;
    }
    public static HashMap<String, CommandeRepereCase> getData(String path) {
        HashMap<String, CommandeRepereCase> listeCaseRepere = new HashMap<>();
        try {
            String profil = "";
            int repere = 0;
            int quantite = 0;
            boolean inRepere = false;
            boolean inProfil = false;

            //String path = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Source\\254210.csv";
            String idCRC = "";
            //System.out.println("Entre file path:");
            //String filePath = scanner.nextLine();
            //FileReader fileReader = new FileReader(filePath);
            FileReader fileReader = new FileReader(path);
            Scanner fileScanner = new Scanner(fileReader);

            while(fileScanner.hasNext()) {
                String fileLine = fileScanner.nextLine();
                
                // Récupération du numéro de lot et du numéro de commande
                if(fileLine.contains("LOT")) {
                    lot = Integer.parseInt(fileLine.split(";")[1]);
                    commande = fileLine.split(";")[3];
                    inRepere = false;
                    inProfil = false;
                }

                // Récupération du numéro de repère et de la quantité d'un repère
                if(fileLine.contains("Rep")) {
                    inRepere = true;
                    inProfil = false;
                    String texteRepere = fileLine.split(";")[0];
                    String texteQuantite = fileLine.split(";")[1];
                    repere = Integer.parseInt(texteRepere.split("re")[1]);
                    quantite = Integer.parseInt(texteQuantite.split(" : ")[1]);
                    continue;
                }
                
                // Récupération du profil d'un repère
                if(fileLine.contains("PROFIL")) {
                    inProfil = true;
                    inRepere = false;
                    profil = fileLine.substring(8).replace(" ", "");
                    
                    for (String key : listeCaseRepere.keySet()) {
                        if (key.contains(commande+"-"+repere)) {
                            listeCaseRepere.get(key).setProfil(profil);
                        }
                    }
                    continue;
                }
                
                // Récupération des dimensions d'une case d'un repère
                if(inRepere) {
                    int caseNumber = Integer.parseInt(fileLine.split(";")[0].trim());
                    String dimension = fileLine.split(";")[1].replace(" * ", "x").replace(",",".");
                    double caseLargeur = Double.parseDouble(dimension.split("x")[0]);
                    double caseHauteur = Double.parseDouble(dimension.split("x")[1]);
                    double caseHauteurPoignee = 0.0;
                    try {
                        caseHauteurPoignee = Double.parseDouble(fileLine.split(";")[2].replace(",","."));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
                    idCRC = commande+"-"+repere+"-"+caseNumber;
                    listeCaseRepere.put(idCRC, new CommandeRepereCase(repere, caseNumber, caseLargeur, caseHauteur, caseHauteurPoignee, lot, commande, profil));
                }

                // Récupération de tous les détails d'un repère, toutes case confondue
                if(inProfil) {
                    int OT = Integer.parseInt(fileLine.split(";")[1]);
                    String findLine = commande+"-"+repere+"-"+OT;
                    CommandeRepereCase crc = listeCaseRepere.get(findLine);

                    char position = fileLine.split(";")[3].charAt(0);
                    String code = fileLine.split(";")[5];
                    String codeFerrureEnum = "_"+code.replace("-", "_");
                    int quantiteCode = Integer.parseInt(fileLine.split(";")[7]);
                    if (quantite != 0) quantiteCode /= quantite;
                    Double coupe = Double.parseDouble(fileLine.split(";")[9].replace(",", "."));
                    
                    // exception on file generation
                    if(code.equals("222214"))position = 'H';
                    // codeExists
                    try {
                        Ferrure ferrure = Ferrure.valueOf(codeFerrureEnum);
                        
                        String designation = ferrure.getDesignation();
                        TypeFerrure typeFerrure = ferrure.getType();
                        ArrayList<Double> trouDeVisList = new ArrayList<>();

                        // TODO:
                        // CREMONE, RALLONGE, COMPAS
                        // Ajouter un trou de vis à -8mm si coupe inférieur à la dernière valeur de la liste des trou de vis

                        // TODO:
                        // pour STB ajouter un trou a +8.0mm -> a faire dans la DB [taille + 8.0mm]
                        // pour STB retirer les 2 premiers trou de vis de la crémone [coupe = 0.0]

                        // TODO:
                        // Ajouter les conditions VERROU SERRURE CREMONE_SOUFFLET
                        // PAS DE COUPE
                        // PAS D'ASSEMBLAGE

                        // En attente de consigne !
                        // TODO:
                        // Vérifier si la coupe n'est pas entre la dernière vis 
                        // Peut importe la coupe, si le trou de vis = coupe - 8 mm alors pas de réel coupe.
                        for(Double trouDeVis : ferrure.getTrouDeVis()) {
                            if (trouDeVis < coupe || coupe <= 0.0) {
                                trouDeVisList.add(trouDeVis);
                            } else {
                                if(ferrure.getType() != TypeFerrure.RAB && ferrure.getType() != TypeFerrure.RAH) {
                                    if(ferrure.getType() != TypeFerrure.STB) {
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

                        // ajouter le detail
                        for(int i = 0; i < quantiteCode;i++){
                            Detail detail = new Detail(position,code,coupe,trouDeVisList,designation,typeFerrure);
                            crc.addDetails(detail);
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                //System.out.println(fileLine);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error on data extraction: " + e.getMessage());
        }
        return listeCaseRepere;
    }
    public static void writeFile(HashMap<String, CommandeRepereCase> listeCaseRepere){
        try (PrintWriter printWriter = new PrintWriter(new FileWriter("../test/"+lot+".ba2"))) {
            for (String cr : listeCaseRepere.keySet()) {
                //System.out.println(CommandeRepereCase.generateCAD(listeCaseRepere.get(cr)));
                //CommandeRepereCase.toString(listeCaseRepere.get(cr));
                printWriter.println(CommandeRepereCase.writeToFile(listeCaseRepere.get(cr)));
            }
            printWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    public static void writeCurrentFile(String completFile) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(currentFile))) {
            printWriter.print(completFile.split(".csv")[0]);
            printWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    public static void main(String[] args) {

        currentLot = getCurrentLot(currentFile);
        ArrayList<String> fileNames = getUncompletFile(new File(sourcePath));

        for (String uncompletFile : fileNames) {
            System.out.println(sourcePath+uncompletFile);
            HashMap<String, CommandeRepereCase> listeCaseRepere = getData(sourcePath+uncompletFile);
            writeFile(listeCaseRepere);
            listeCaseRepere.clear();
            //writeCurrentFile(uncompletFile);
        }
	}
}