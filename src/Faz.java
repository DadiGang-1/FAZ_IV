import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;


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

            String jsonContent = "";

            try {
                FileReader jsonFileReader = new FileReader("C:\\Users\\david\\OneDrive - ALU PVC CREATION\\Bureau\\LOT_FAZ_IV\\Code\\FAZ_IV\\src\\ferrureList.json");
                Scanner jsonScanner = new Scanner(jsonFileReader);
                
                while (jsonScanner.hasNext()) {
                    jsonContent += jsonScanner.nextLine();
                }
                jsonScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("JSON file not found: " + e.getMessage());
            }

            while(fileScanner.hasNext()) {
                String fileLine = fileScanner.nextLine();
                
                if(fileLine.contains("LOT")) {
                    lot = Integer.parseInt(fileLine.split(";")[1]);
                    commande = fileLine.split(";")[3];
                }

                if(fileLine.contains("Rep")) {
                    inRepere = true;
                    inProfil = false;
                    String texteRepere = fileLine.split(";")[0];
                    String texteQuantite = fileLine.split(";")[1];
                    repere = Integer.parseInt(texteRepere.split("re")[1]);
                    quantite = Integer.parseInt(texteQuantite.split(" : ")[1]);
                    continue;
                }
                
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
                
                if(inRepere) {
                    int caseNumber = Integer.parseInt(fileLine.split(";")[0].trim());
                    String dimension = fileLine.split(";")[1].replace(" * ", "x").replace(",",".");
                    double caseLargeur = Double.parseDouble(dimension.split("x")[0]);
                    double caseHauteur = Double.parseDouble(dimension.split("x")[1]);
                    idCRC = commande+"-"+repere+"-"+caseNumber;
                    listeCaseRepere.put(idCRC, new CommandeRepereCase(repere, caseNumber, caseLargeur, caseHauteur, lot, commande, profil));
                }

                if(inProfil) {
                    int OT = Integer.parseInt(fileLine.split(";")[1]);
                    String findLine = commande+"-"+repere+"-"+OT;
                    CommandeRepereCase crc = listeCaseRepere.get(findLine);

                    char position = fileLine.split(";")[3].charAt(0);
                    String code = fileLine.split(";")[5];
                    Double coupe = Double.parseDouble(fileLine.split(";")[9].replace(",", "."));
                    
                    boolean codeExists = false;

                    // Vérifier si le code existe dans le json
                    String codeSearch = "\"code\":\"" + code + "\"";
                    String coupeSearch = "\"trouDeVis\":[";
                    codeExists = jsonContent.contains(codeSearch);
                    
                    if (codeExists) {
                        int codeIndex = jsonContent.indexOf(codeSearch) + codeSearch.length();
                        int coupeIndex = jsonContent.indexOf(coupeSearch, codeIndex) + coupeSearch.length();
                        String checkEndLine = jsonContent.substring(codeIndex, coupeIndex);

                        // recuperer la designation
                        String designationSearchStart = "\"designation\":\"";
                        String designationSearchEnd = "\"";
                        int designationStartIndex = jsonContent.indexOf(designationSearchStart,codeIndex) + designationSearchStart.length();
                        int designationEndIndex = jsonContent.indexOf(designationSearchEnd, designationStartIndex);
                        String designation = jsonContent.substring(designationStartIndex, designationEndIndex);
                        System.out.println(designation);
                        if (!checkEndLine.contains("}")) {
                            int endIndex = jsonContent.indexOf("]", coupeIndex);
                            String trouDeVisString = jsonContent.substring(coupeIndex, endIndex);
                            ArrayList<Double> trouDeVisList = new ArrayList<>();
                            
                            for (int i = 0; i < trouDeVisString.split(",").length; i++) {
                                Double trouDeVisValue = Double.parseDouble(trouDeVisString.split(",")[i].replace(" ", ""));
                                if (trouDeVisValue < coupe || coupe <= 0.0){
                                    trouDeVisList.add(trouDeVisValue);
                                } else {
                                    continue;
                                }
                            }
                            // Que faire si les quantités du détail est de plus de 1?
                            Detail detail = new Detail(position,code,coupe,trouDeVisList,designation);
                            crc.addDetails(detail);
                        }
                    }
                }

                //System.out.println(fileLine);
            }

            try (FileWriter fileWriter = new FileWriter("../test/file.ba2")) {
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for (String cr : listeCaseRepere.keySet()) {
                    //System.out.println(CommandeRepereCase.generateCAD(listeCaseRepere.get(cr)));
                    CommandeRepereCase.toString(listeCaseRepere.get(cr));
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