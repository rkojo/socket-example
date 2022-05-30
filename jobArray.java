import java.util.ArrayList;

//used to store jobs
public class jobArray {
    ArrayList<jobs> list;

    public jobArray() {
        list = new ArrayList<jobs>();
    }

    public void add(jobs jobs) {
        list.add(jobs);
    }
}
