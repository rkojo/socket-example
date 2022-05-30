import java.util.ArrayList;

public class customfit {
    ArrayList<Server> serverList;
    ArrayList<Server> serverarr;
    int cores;

    public customfit() {
    }


    public Server returnServer(ArrayList<Server> serverList, int cores) {
        serverList.sort(new serverCompare());
        Server s = null;

        for (int i = 0; i < serverList.size(); i++) {
            // just turn all servers on - this takes time so open all no matter the fitness
            // also if the server is empty
            if (serverList.get(i).rjobs == 0 && serverList.get(i).wjobs == 0) {
                s = serverList.get(i);
                break;
            }
        }
        if (s == null) {
            // if there is a server that has more available (server is available)
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).cores >= cores) {
                    s = serverList.get(i);
                    // break;
                }
            }
        }
        if (s == null) {
            // if there is an open server
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).state.equals("idle")) {
                    s = serverList.get(i);
                    break;
                }
            }
        }
        if (s == null) {
            // if there is a server with empty space available and able to start the job
            // immediately
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).state.equals("active") && serverList.get(i).wjobs == 0) {
                    s = serverList.get(i);
                    break;
                }
            }
        }
        if (s == null) {
            s = serverList.get(0);
            // otherwise, the least waited server
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).wjobs < s.wjobs) {
                    s = serverList.get(i);
                    break;
                }
            }
        }

        return s;
    }

    public Server migrateServer(ArrayList<Server> available, Server bestServer) {
        for (int k = 0; k < available.size(); k++) {
            // if open server
            if (available.get(k).state.equals("idle")) {
                bestServer = available.get(k);
                break;
            } else
            // can currently take in jobs
            if (available.get(k).state.equals("active") && available.get(k).wjobs == 0) {
                bestServer = available.get(k);
                
            } else
            // less jobs and less cores required in the server.
            if ((available.get(k).wjobs < bestServer.wjobs && available.get(k).rjobs == 0 && available.get(k).cores <= bestServer.cores)) {
                bestServer = available.get(k);
            }

        }
        return bestServer;
    }

}
