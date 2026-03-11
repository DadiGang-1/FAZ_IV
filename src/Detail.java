import java.util.ArrayList;

public class Detail{
    char position;
    String code;
    Double coupe;
    ArrayList<Double> trouDeVisList;
    String designation;

    public Detail(char position, String code, Double coupe, ArrayList<Double> trouDeVisList, String designation) {
        this.position = position;
        this.code = code;
        this.coupe = coupe;
        this.trouDeVisList = trouDeVisList;
        this.designation = designation;
    }
}
