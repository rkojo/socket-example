import java.util.ArrayList;

public final class serverArray {
    ArrayList<Server> servers;
    //used to store the values of the biggest servers. This is done as gets capable reduces the cores if a job is running. 
    public serverArray(){
        servers = null;
    }

    public serverArray(ArrayList<Server> a) {
        servers = a;
    }
    //return the best server = the best servers are already in the class, so the first one is sent.
    public Server returnBest() {
        return servers.get(0);
    }
    //return the type = name of server
    public String returnString() {
        return servers.get(0).type;
    }
    //return name of server based on the value to schedule jobs - used in ln 73 in client.
    public String returnValue(int i) {
        return servers.get(i).type;
    }
    //return the id of server to schedule jobs- used in ln 73 in client
    public int returnInt(int i ) {
        return servers.get(i).id;
    }
    //used to ensure that count does not go over this list.
    public int size() {
        return servers.size();
    }
}
