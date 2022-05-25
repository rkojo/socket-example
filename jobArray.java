import java.util.ArrayList;

public class jobArray {
    ArrayList<jobs> list;
    
    public jobArray() {
        list = new ArrayList<jobs>();
    }

    public void add(jobs jobs) {
        list.add(jobs);
    }
}
