import java.util.ArrayList;

public class CommandeRepereCase extends Faz{
    int caseNumber;
    int repereNumber;
    double largeur;
    double hauteur;
    int lot;
    String commande;
    String profil;
    
    ArrayList<Detail> detailsList = new ArrayList<>();

    public CommandeRepereCase(int repereNumber, int caseNumber, double largeur, double hauteur, int lot, String commande, String profil) {
        this.repereNumber = repereNumber;
        this.caseNumber = caseNumber;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.lot = lot;
        this.commande = commande;
        this.profil = profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
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

        // [1-4]
        sb.append(generateCAD(caseRepere));
        // [5]
        // TODO: 
        // Définir une règle pour savoi si c'est SX ou DX
        // Comprendre le balise check de la notice graf
        sb.append("\tSX ou DX\t"+caseRepere.largeur+"\t"+caseRepere.hauteur+"\t"+caseRepere.profil+","+caseRepere.profil+","+caseRepere.profil+","+caseRepere.profil+"\t\t\t"+caseRepere.caseNumber+"\t\t\t\t\t"+caseRepere.commande+"\t"+caseRepere.repereNumber+"\t\t\n");
        // [6-7]
        // TODO:
        // Comprendre ce qui faut mettre dans ces lignes
        sb.append("position paumelles côté paumelles\nposition paumelles coté bas (si soufflet)\n");
        // [8-9]
        sb.append("0,0,0\n0,0,0\n");
        // [10-13]
        // TODO:
        // En attentes de réponse
        // [Detail:01-04]
        sb.append(writeDetails(caseRepere.detailsList));


        //sb.append("Profil: "+caseRepere.profil+" Repere: "+caseRepere.repereNumber+" Case: "+caseRepere.caseNumber+" Largeur: "+caseRepere.largeur+" Hauteur: "+caseRepere.hauteur+" Lot: "+caseRepere.lot+" Commande: "+caseRepere.commande+"\n");
        //for (Detail detail : caseRepere.detailsList) {
        //    sb.append("Position: "+detail.position+" Code: "+detail.code+" Coupe: "+detail.coupe+"\n");
        //    for(Double trouDeVis : detail.trouDeVisList) {
        //        sb.append("Trou de vis: "+trouDeVis+"\n");
        //    }
        //}

        return sb.toString();
    }

    public static String writeDetails(ArrayList<Detail> detailsList) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb01 = new StringBuilder();
        StringBuilder sb02 = new StringBuilder();
        StringBuilder sb03 = new StringBuilder();
        StringBuilder sb04 = new StringBuilder();


        //TODO:
        // Les éléments d'angle doivent être indiqués dans le groupe 02(B) ou 04(H) et jamais dans le groupe 01 ou 03.
        // Si un côté ne nécessite pas de vis, il doit contenir au moins un élément avec toutes les valeurs à 0, exemple: 0,0,0,0

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
        return sb.toString();
    }
}