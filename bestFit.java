import java.util.ArrayList;

public class bestFit implements serverImplementation{
    ArrayList<Server> serverList;
    int cores;
    public bestFit (ArrayList<Server> servers, int cores) {
        serverList = servers;
        this.cores = cores;
    }


    public Server returnServer() {
        Server s = null;
        int fitness = cores ;
        for(int i = 0; i<serverList.size(); i++) {
            if(serverList.get(i).rjobs == 0 && serverList.get(i).wjobs == 0 && ((serverList.get(i).cores - cores) < fitness)) {
                s = serverList.get(i);
                fitness = s.cores - cores;
            }
        }
        //only happens if there are no servers that have no jobs
        if(s == null) {
            //gets the smallest server
            s = serverList.get(0);
        }
        return s;
    }   

}
