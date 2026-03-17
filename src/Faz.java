import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

// Autre type pour créer les fichiers .ba2:
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

class Faz {

    private static int lot = 0;
    private static String commande = "";

    
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

        HashMap<String, CommandeRepereCase> listeCaseRepere = new HashMap<>();

        try {
            String profil = "";
            int repere = 0;
            int quantite = 0;
            boolean inRepere = false;
            boolean inProfil = false;
            String path = "C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Source\\254166.csv";
            String idCRC = "";
            System.out.println("Entre file path:");
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
                    quantiteCode /= quantite; // TODO: Add exception div by 0;
                    Double coupe = Double.parseDouble(fileLine.split(";")[9].replace(",", "."));

                    // codeExists
                    try {
                        Ferrure ferrure = Ferrure.valueOf(codeFerrureEnum);
                        
                        String designation = ferrure.getDesignation();
                        TypeFerrure typeFerrure = ferrure.getType();
                        ArrayList<Double> trouDeVisList = new ArrayList<>();

                        // TODO:
                        // Pour Crémone, Rallonge, Compas
                        // ajouter un trou de vis à -8mm si coupe inférieur à la dernière valeur de la liste des trou de vis
                        // pour STB ajouter un trou a +8mm -> a faire dans la DB

                        // TODO:
                        // ajouter le condition VERROU SERRURE CREMONE_SOUFFLET
                        // IL NE SE RECOUPE PAS 
                        // PAS D'ASSEMBLAGE QUI SUIT

                        // En attente de consigne !
                        for(Double trouDeVis : ferrure.getTrouDeVis()) {
                            if (trouDeVis < coupe || coupe <= 0.0) {
                                trouDeVisList.add(trouDeVis); 
                                // STB -> coupe = 0.0 mais je doit ajouter un trou à [taille + 8.0mm]
                                // TODO: 
                                // ajouter le trou de vis dans la base de données
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

                        //if (ferrure.getType() == TypeFerrure.RAB || ferrure.getType() == TypeFerrure.RAH) {
                        //    // garder toutes les côtes de la liste
                        //    for(Double trouDeVis : ferrure.getTrouDeVis()) {
                        //        trouDeVisList.add(trouDeVis);
                        //    }
                        //} else {
                        //    // retirer les côté supérieur à la coupe et ajouter un nouvel élément a +8mm
                        //    for(Double trouDeVis : ferrure.getTrouDeVis()) {
                        //        if (trouDeVis < coupe || coupe <= 0.0) {
                        //            trouDeVisList.add(trouDeVis);
                        //        } else {
                        //            if(ferrure.getType() != TypeFerrure.RAB && ferrure.getType() != TypeFerrure.RAH) {
                        //                trouDeVisList.add(trouDeVis+8);
                        //                break;
                        //            }
                        //        }
                        //    }
                        //}

                        for(int i = 0; i < quantiteCode;i++){
                            // ajouter le detail
                            Detail detail = new Detail(position,code,coupe,trouDeVisList,designation,typeFerrure);
                            crc.addDetails(detail);
                        }

                        
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }

                //System.out.println(fileLine);
            }

            // TODO:
            // Changer par java.nio.file pour créer le fichier .ba2

            //File file = new File("../test/file.ba2");
            //if(!file.exists()) {
            //    String fileContent = "";
            //    for (String cr : listeCaseRepere.keySet()) {
            //        fileContent = CommandeRepereCase.writeToFile(listeCaseRepere.get(cr));
            //    }
            //    try {
            //        file.createNewFile();
            //        
            //    } catch (IOException e) {
            //        System.out.println("An error occurred while creating the file: " + e.getMessage());
            //    }
            //}

            try (FileWriter fileWriter = new FileWriter("../test/file.ba2")) {
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for (String cr : listeCaseRepere.keySet()) {
                    //System.out.println(CommandeRepereCase.generateCAD(listeCaseRepere.get(cr)));
                    //CommandeRepereCase.toString(listeCaseRepere.get(cr));
                    printWriter.println(CommandeRepereCase.writeToFile(listeCaseRepere.get(cr)));
                }
                printWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file: " + e.getMessage());
            }

            fileScanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } finally {
            scanner.close();
        }
	}
}