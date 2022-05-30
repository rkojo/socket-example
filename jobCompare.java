import java.util.Comparator;

public class jobCompare implements Comparator<jobs> {
//compares in descending order
    @Override
    public int compare(jobs arg0, jobs arg1) {
        return arg1.estRunttime - arg0.estRunttime;
    }
    
}