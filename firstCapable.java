import java.util.ArrayList;

public class firstCapable implements serverImplementation {
    ArrayList<Server> serverList;
    public firstCapable(ArrayList<Server> servers) {
        serverList = servers;
    }

    public Server returnServer() {
        return serverList.get(0);
    }
}
