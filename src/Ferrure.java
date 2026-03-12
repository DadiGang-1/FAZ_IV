public enum Ferrure {
    
    _203697("203697","M Rallonge recoup BTV Mm L_497 1G",new double[]{7,117},false,false,true,false,false,false,false),
    _203698("203698","M Rallonge recoup BTV Mm L_747 1G",new double[]{7,117,367},false,false,false,true,false,false,false);


    private String code;
    private String designation;
    private double[] trouDeVis;
    private boolean RAB;
    private boolean RAH;
    private boolean rallonge;
    private boolean cremone; // Côté crémone -> 03
    private boolean serrure;
    private boolean verrou; // Côté crémone -> 03
    private boolean compas;

    Ferrure(String code, String designation, double[] trouDeVis, boolean RAB, boolean RAH, boolean rallonge, boolean cremone, boolean serrure, boolean verrou, boolean compas) {
        this.code = code;
        this.designation = designation;
        this.trouDeVis = trouDeVis;
        this.RAB = RAB;
        this.RAH = RAH;
        this.rallonge = rallonge;
        this.cremone = cremone;
        this.serrure = serrure;
        this.verrou = verrou;
        this.compas = compas;
    }

    public String getCode(){
        return code;
    }
    public String getDesignation(){
        return designation;
    }
    public double[] getTrouDeVis(){
        return trouDeVis;
    }
    public boolean isRAB(){
        return RAB;
    }
    public boolean isRAH(){
        return RAH;
    }
    public boolean isRallonge(){
        return rallonge;
    }
    public boolean isCremone(){
        return cremone;
    }
    public boolean isSerrure(){
        return serrure;
    }
    public boolean isVerrou(){
        return verrou;
    }
    public boolean isCompas(){
        return compas;
    }
}
