import java.util.Comparator;

public class serverCompare implements Comparator<Server> {

    //compares in ascending order
    @Override
    public int compare(Server s1, Server s2) {

    return s1.wjobs- s2.wjobs;
    }

}
