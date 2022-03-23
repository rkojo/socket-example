public class Server {
    String type;
    int limit;
    int bootupTime;
    Double hourlyRate;
    int cores;
    int memory;
    int disk;

    public Server(String t, int l, int b, Double h, int c, int m, int d) {
        type = t;
        limit = l;
        bootupTime = b;
        hourlyRate = h;
        cores = c;
        memory = m;
        disk = d;
    }
}
