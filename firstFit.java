import java.util.ArrayList;

public class firstFit implements serverImplementation {
    ArrayList<Server> serverList;
    public firstFit (ArrayList<Server> servers) {
        serverList = servers;
    }
    public Server returnServer() {
        Server s = null;
        boolean barrier = true;
        for(int i = 0; i<serverList.size(); i++) {
            if((serverList.get(i).wjobs == 0 && serverList.get(i).rjobs == 0)) {
                if(barrier == true) {
                s = serverList.get(i);
                barrier = false;
                }
                //break;
            }
        }
        if(s == null) {
            s = serverList.get(0);
            }
        return s;
    }
}
