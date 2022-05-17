import java.util.ArrayList;

public class bestFit implements serverImplementation{
    ArrayList<Server> serverList;
    ArrayList<Server> serverarr;
    int cores;
    public bestFit (ArrayList<Server> servers, ArrayList<Server> serverarr, int cores) {
        serverList = servers;
        this.serverarr = serverarr;
        this.cores = cores;
    }

    public Server returnServer() {
        Server s = null;
        Integer fitness = 999 ;
        System.out.println(cores);
        for(int i = 0; i<serverList.size(); i++) {
            System.out.println(serverList.get(i).type+serverList.get(i).id+serverList.get(i).wjobs + "waiting list" );
            if(serverList.get(i).rjobs == 0 && serverList.get(i).wjobs == 0 && ((serverList.get(i).cores - cores) < fitness)) {
                s = serverList.get(i);
                fitness = s.cores - cores;
            }
        }
        //only happens if there are no servers that have no jobs
        if(s == null) {

            //gets the smallest server
           for(int i = 0; i < serverarr.size(); i++) {
              if((serverarr.get(i).cores - cores < fitness) && serverarr.get(i).cores - cores >= 0) {
                  s = serverarr.get(i);
              }
           }
                }
        return s;
    }   
}
