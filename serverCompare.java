import java.util.Comparator;

public class serverCompare implements Comparator<Server> {

    //compares in descending order
    @Override
    public int compare(Server s1, Server s2) {
    //   if(s1.cores == s2.cores) {
    //     return 0;
    //   } else if(s1.cores >s2.cores) {
    //     return -1;
    //   } else {
    //     return 1;
    //   }
    return s1.cores - s2.cores;
    }

}
