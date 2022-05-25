import java.util.Comparator;

public class jobCompare implements Comparator<jobs> {

    @Override
    public int compare(jobs arg0, jobs arg1) {
        return arg0.core - arg1.core;
    }
    
}