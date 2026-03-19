import java.util.ArrayList;
import java.util.LinkedList;

public class CommandeRepereCase {
    int caseNumber;
    int repereNumber;
    double largeur;
    double hauteur;
    double hauteurPoignee;
    int lot;
    String commande;
    String profil;
    
    ArrayList<Detail> detailsList = new ArrayList<>();

    public CommandeRepereCase(int repereNumber, int caseNumber, double largeur, double hauteur, double hauteurPoignee, int lot, String commande, String profil) {
        this.repereNumber = repereNumber;
        this.caseNumber = caseNumber;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.hauteurPoignee = hauteurPoignee;
        this.lot = lot;
        this.commande = commande;
        this.profil = profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }
    
    public int getLot() {
        return lot;
    }

    public double getHauteurPoignee() {
        return hauteurPoignee;
    }
    
    public void addDetails(Detail detail) {
        detailsList.add(detail);
    }

    public static String generateCAD(CommandeRepereCase caseRepere) {
        String paddedLot = "000000" + caseRepere.lot;
        String lotString = paddedLot.substring(paddedLot.length() - 6);

        String commandeString = caseRepere.commande.substring(2, 6);
    
        String paddedRepere = "00" + caseRepere.repereNumber;
        String repereString = paddedRepere.substring(paddedRepere.length() - 2);

        String chassis = String.valueOf(caseRepere.caseNumber);
        
        String cad = "";
        cad = lotString.concat(commandeString).concat(repereString).concat(chassis).concat("00001\n");
        cad += lotString.concat(commandeString).concat(repereString).concat(chassis).concat("00002\n");
        cad += lotString.concat(commandeString).concat(repereString).concat(chassis).concat("00003\n");
        cad += lotString.concat(commandeString).concat(repereString).concat(chassis).concat("00004\n");

        return cad;
    }

    public static void toString(CommandeRepereCase caseRepere) {
        System.out.println("Profil: "+caseRepere.profil+" Repere: "+caseRepere.repereNumber+" Case: "+caseRepere.caseNumber+" Largeur: "+caseRepere.largeur+" Hauteur: "+caseRepere.hauteur+" Lot: "+caseRepere.lot+" Commande: "+caseRepere.commande);
        for (Detail detail : caseRepere.detailsList) {
            System.out.println("Position: "+detail.position+" Code: "+detail.code+" Coupe: "+detail.coupe);
            for(Double trouDeVis : detail.trouDeVisList) {
                System.out.println("Trou de vis: "+trouDeVis);
            }
        }
    }

    public static String writeToFile(CommandeRepereCase caseRepere) {
        StringBuilder sb = new StringBuilder();
        String openingType = openingType(caseRepere.detailsList);
        // [1-4]
        sb.append(generateCAD(caseRepere));
        // [5]
        // TODO:
        // Comprendre le balise check de la notice graf
        sb.append("\t"+openingType+"\t"+caseRepere.largeur+"\t"+caseRepere.hauteur+"\t"+caseRepere.profil+","+caseRepere.profil+","+caseRepere.profil+","+caseRepere.profil+"\t\t\t"+caseRepere.caseNumber+"\t\t\t\t\t"+caseRepere.commande+"\t"+caseRepere.repereNumber+"\t\t\n");
        // [6-7]
        // TODO:
        // Comprendre ce qui faut mettre dans ces lignes
        sb.append("position paumelles côté paumelles\n");
        sb.append("position paumelles coté bas (si soufflet)\n");
        // [8-9]
        sb.append("0,0,0\n0,0,0\n");
        // [10-13]
        // TODO:
        // En attentes de réponse
        sb.append(getList(caseRepere.detailsList, openingType, "01", caseRepere.hauteur)+"\n"); // PAUMELLE
        sb.append(getList(caseRepere.detailsList, openingType, "02", caseRepere.largeur)+"\n"); // [B]
        sb.append(getList(caseRepere.detailsList, openingType, "03", caseRepere.hauteur)+"\n"); // CREMONE
        sb.append(getList(caseRepere.detailsList, openingType, "04", caseRepere.largeur)+"\n"); // [H]

        // [Detail:01-04]
        sb.append(writeDetails(caseRepere.detailsList));

        return sb.toString();
    }

    public static String writeDetails(ArrayList<Detail> detailsList) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb01 = new StringBuilder();
        StringBuilder sb02 = new StringBuilder();
        StringBuilder sb03 = new StringBuilder();
        StringBuilder sb04 = new StringBuilder();


        // TODO:
        // Les éléments d'angle doivent être indiqués dans le groupe 02(B) ou 04(H) et jamais dans le groupe 01 ou 03.
        // Si un côté ne nécessite pas de vis, il doit contenir au moins un élément avec toutes les valeurs à 0, exemple: 0,0,0,0

        // TODO:
        // si ferrure avec une coupe à 0.0 que faire ?

        // TODO:
        // Tous les RAB doivent être désigner en bas
        // Tous les RAH doivent être désigner en haut

        // TODO:
        // Repèrer tous les éléments avec un type crémone 
        // Cela deviendre le côté crémone 03
        // Ainsi le côté opposé devient le côté paumelle
        

        for(Detail detail : detailsList) {
            switch (detail.position) {
                case 'D' :
                    sb01.append("01\t"+detail.code+"\t"+detail.designation+"\t"+"0,"+detail.coupe+",0\n");
                    break;
                case 'B' :
                    sb02.append("02\t"+detail.code+"\t"+detail.designation+"\t"+"0,"+detail.coupe+",0\n");
                    break;
                case 'G' :
                    sb03.append("03\t"+detail.code+"\t"+detail.designation+"\t"+"0,"+detail.coupe+",0\n");
                    break;
                case 'H' :
                    sb04.append("04\t"+detail.code+"\t"+detail.designation+"\t"+"0,"+detail.coupe+",0\n");
                    break;
            }
        }

        // Ajouter vérification pour inverser sb01 et sb03
        sb.append(sb01.toString()+sb02.toString()+sb03.toString()+sb04.toString());

        sb.deleteCharAt(sb.toString().length() - 1);
        return sb.toString();
    }

    public static String openingType(ArrayList<Detail> detailsList) {
        for(Detail detail : detailsList) {
            if(detail.typeFerrure == TypeFerrure.CREMONE || detail.typeFerrure == TypeFerrure.SERRURE || detail.typeFerrure == TypeFerrure.VERROU) {
                switch (detail.position) {
                    case 'H':
                        return "AX";
                    case 'B':
                        return "GX";
                    case 'G':
                        return "SX";
                    case 'D':
                        return "DX";
                    default:
                        break;
                }
            }
        }
        return "_X";
    }

    public static String getList(ArrayList<Detail> detailsList, String openingType, String side, Double Length) {
        // details, DX, 01, 500.0

        // TODO:
        // gérer les éléments d'angle, cote inversé
        // mettre tous les angles en 02 ou 04 

        //  Ordre d'installation côté crémone
        //      RAB > CREMONE > RALLONGE > RAH
        // 
        //  Ordre d'installation côté haut
        //      RAH(coté paumelle) > COMPAS > RALLONGE > RAH(coté crémone)
        //
        //  Ordre d'installation côté paumelle
        //      RAH > RALLONGE (1 max.)
        //
        //  Ordre d'installation côté bas
        //      RAB


        // y'a-t-il un RAB ?
        // si oui commence par ça 
        // y'a-t-il un RAH ?

        // TODO: 
        // Récupérer tous les éléments côté D
        // Si les RAB/RAH sont déclarer sur D alors ajouter les côtes
        // Sinon ajouter les côtes 222214 uiquement

        switch (openingType) {
            case "DX":
                switch (side) {
                    case "01": // [G] PAUMELLE
                        return getListDX01(detailsList,Length);
                    case "02": // [B]
                        return getListDX02(detailsList,Length);
                    case "03": // [D] CREMONE
                        return getListDX03(detailsList,Length);
                    case "04": // [H]
                        return getListDX04(detailsList,Length);
                    default:
                        break;
                }
                break;

            case "SX":
                switch (side) {
                    case "01": // [D] PAUMELLE
                        return getListSX01(detailsList,Length);
                    case "02": // [B]
                        return getListSX02(detailsList,Length);
                    case "03": // [G] CREMONE
                        return getListSX03(detailsList,Length);
                    case "04": // [H]
                        return getListSX04(detailsList,Length);
                    default:
                        break;
                }
                break;

            case "AX":
                break;
            case "BX":
                break;
            default:
                break;
        }
        return null;
    }

    public static String getListDX01(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;

            if(detail.position == 'G') {
                for (Double trouDeVis : detail.trouDeVisList) {
                    dimTrouDeVis = actualLength + trouDeVis;
                    holeList.add(dimTrouDeVis);
                    System.out.println("DIM G "+detail.typeFerrure+" : "+dimTrouDeVis);
                }
                actualLength = dimTrouDeVis;
            }
        }
        return holeListToString(holeList);
    }
    public static String getListDX02(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            if(detail.position == 'B' || detail.typeFerrure == TypeFerrure.RAB) {
                for (Double trouDeVis : detail.trouDeVisList) {
                    dimTrouDeVis = actualLength + trouDeVis;
                    holeList.add(dimTrouDeVis);
                    System.out.println("DIM B "+detail.typeFerrure+" : "+dimTrouDeVis);
                }
                actualLength = dimTrouDeVis;
            }
        }
        return holeListToString(holeList);
    }
    public static String getListDX03(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            switch (detail.typeFerrure) {
                case TypeFerrure.RAH:
                    if(!detail.code.equals("222214")) {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = Length - trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("DIM D OPP "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                    }
                    break;
                default:
                    if(detail.typeFerrure == TypeFerrure.RAB || detail.position == 'D') {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("DIM D "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
            }
        }
        return holeListToString(holeList);
    }
    public static String getListDX04(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            switch (detail.typeFerrure) {
                case TypeFerrure.RAH:
                    if(!detail.code.equals("222214")) {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = Length - trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("DIM H OPP "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                    } else {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("DIM H "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
                default:
                    if(detail.position == 'H') {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("DIM H "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
            }
        }
        return holeListToString(holeList);
    }

    public static String getListSX01(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;

            if(detail.position == 'D' || detail.code.equals("222214")) {
                for (Double trouDeVis : detail.trouDeVisList) {
                    dimTrouDeVis = actualLength + trouDeVis;
                    holeList.add(dimTrouDeVis);
                    System.out.println("SX DIM D "+detail.typeFerrure+" : "+dimTrouDeVis);
                }
                actualLength = dimTrouDeVis;
            }
        }
        return holeListToStringReversed(holeList,Length);
    }
    public static String getListSX02(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            if(detail.position == 'B' || detail.typeFerrure == TypeFerrure.RAB) {
                for (Double trouDeVis : detail.trouDeVisList) {
                    dimTrouDeVis = actualLength + trouDeVis;
                    holeList.add(dimTrouDeVis);
                    System.out.println("SX DIM B "+detail.typeFerrure+" : "+dimTrouDeVis);
                }
                actualLength = dimTrouDeVis;
            }
        }
        return holeListToStringReversed(holeList,Length);
    }
    public static String getListSX03(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            switch (detail.typeFerrure) {
                case TypeFerrure.RAH:
                    if(!detail.code.equals("222214")) {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = Length - trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("SX DIM G OPP "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                    }
                    break;
                default:
                    if(detail.typeFerrure == TypeFerrure.RAB || detail.position == 'G') {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("SX DIM G "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
            }
        }
        return holeListToStringReversed(holeList,Length);
    }
    public static String getListSX04(ArrayList<Detail> detailsList, Double Length) {
        LinkedList<Double> holeList = new LinkedList<>();
        Double actualLength = 0.0;
        for (Detail detail : detailsList) {
            Double dimTrouDeVis = 0.0;
            switch (detail.typeFerrure) {
                case TypeFerrure.RAH:
                    if(detail.code.equals("222214")) {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = Length - trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("SX DIM H OPP "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                    } else {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("SX DIM H "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
                default:
                    if(detail.position == 'H') {
                        for (Double trouDeVis : detail.trouDeVisList) {
                            dimTrouDeVis = actualLength + trouDeVis;
                            holeList.add(dimTrouDeVis);
                            System.out.println("SX DIM H "+detail.typeFerrure+" : "+dimTrouDeVis);
                        }
                        actualLength = dimTrouDeVis;
                    }
                    break;
            }
        }
        //holeList.order
        return holeListToStringReversed(holeList,Length);
    }

    public static String holeListToString(LinkedList<Double> holeList){
        holeList.sort(null);
        String holeLine = "";
        for (Double hole : holeList) {
            holeLine += hole+",1,1,1\t";
        }
        if(holeLine == ""){
            holeLine = "0,0,0,0\t";
        }
        return holeLine;
    }
    public static String holeListToStringReversed(LinkedList<Double> holeList,Double Length){
        holeList.sort(null);
        String holeLine = "";
        for (Double hole : holeList) {
            Double newHole = Length - hole;
            holeLine += newHole+",1,1,1\t";
        }
        if(holeLine == ""){
            holeLine = "0,0,0,0\t";
        }
        return holeLine;
    }
}