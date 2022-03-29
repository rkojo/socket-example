import java.util.ArrayList;

public final class serverArray {
    ArrayList<Server> servers;


    public serverArray(){
        servers = null;
    }

    public serverArray(ArrayList<Server> a) {
        servers = a;
    }

    public Server returnBest() {
        return servers.get(0);
    }
    public String returnString() {
        return servers.get(0).type;
    }
    public String returnValue(int i) {
        return servers.get(i).type;
    }
    public int returnInt(int i ) {
        return servers.get(i).id;
    }
    public int size() {
        return servers.size();
    }
}
