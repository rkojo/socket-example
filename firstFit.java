import java.util.ArrayList;

public class firstFit implements serverImplementation {
    ArrayList<Server> serverList;
    public firstFit (ArrayList<Server> servers) {
        serverList = servers;
    }
    public Server returnServer() {
        Server s = null;
        for(int i = 0; i<serverList.size(); i++) {
            if(serverList.get(i).state.equals("active") || serverList.get(i).state.equals("inactive") && (serverList.get(i).wjobs == 0 && serverList.get(i).rjobs == 0)) {
                s = serverList.get(i);
                break;
            }
            if(s == null) {
                s = serverList.get(0);
            }
        }
        return s;
    }
}
