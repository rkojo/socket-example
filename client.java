import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class client {
  public static void main(String args[]) {
    //used to store the values of the biggest servers and keeps them even when looping. 
    try {
      Socket socket = new Socket("127.0.0.1", 50000);
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      // DataInputStream dis = new DataInputStream(socket.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     
      dos.write("HELO\n".getBytes());
      dos.flush();

      String recieve = br.readLine();
      //authentication 
      dos.write("AUTH riku\n".getBytes());
      dos.flush();

      recieve = br.readLine();
      //if none, the whole thing is completed. 
      while (recieve.compareTo("NONE") != 0) {
        //step 5
        dos.write("REDY\n".getBytes());
        dos.flush();
        // get step 6
        String s;
        recieve = br.readLine();
        //jcpl tells status of job, so it should loop until all jobs are done. 
        if(getcommand(recieve).compareTo("JCPL") == 0) {
          while(getcommand(recieve).compareTo("JCPL") == 0) {
            dos.write("REDY\n".getBytes());
            dos.flush();
            recieve = br.readLine();
          }
        }
        //if the value is none, it quits and breaks out of loop.
        if(recieve.compareTo("NONE") == 0) {
          dos.write("QUIT\n".getBytes());
          dos.flush();
          break;
        }
        String job = jobid(recieve); // gets id of job in jobn

          //step 7
          s = "GETS Capable " + jobvalue(recieve) +"\n";
          dos.write(s.getBytes());
          dos.flush();
          //gets the amount of servers
          recieve = br.readLine();
          int howm = howMany(recieve);
          //step 9 - gets the list of servers.
          ArrayList<Server> list = getsCapable(dos, br, howm);
          s = "OK\n";
          dos.write(s.getBytes());
          dos.flush();
          recieve = br.readLine();
          //stores the best servers only. 
        //the best server possible and the id of the server. 

        String bestserver = determineStrategy(list, arg[0], );

        //step 7 - job scheduling
        s = "SCHD " + job + " " + bestserver + "\n";
        dos.write(s.getBytes());
        dos.flush();
        recieve = br.readLine();
      }
      //reads final value.
      String recfinal = br.readLine();
      if (recfinal.equals("QUIT")) {
        dos.close();
        socket.close();
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  //used to return to detect jcpl
  public static String getcommand(String s) {
    String[] arr;
    arr = s.split(" ");
    return arr[0];
  }

  //used to find job requirement for core, disk and memory to use in GETS capable 
  public static String jobvalue(String s) {
    String[] arr;
    arr = s.split(" ", 0);
    return arr[4]+" " + arr[5]+" " + arr[6] + "\n";
  }
  //get the amount of cores required for the job.
  public static int jobCores(String s) {
    String[] arr;
    arr = s.split(" ",0);
    return Integer.parseInt(arr[4]);
  }
  //used to find the id of the job to use to schedule jobs. 
  public static String jobid(String s) {
    String[] arr;
    arr = s.split(" ");
    return arr[2];
  }

  //sees how many capable servers to use in the getscapable loop. 
  public static int howMany(String s) {
    String[] arr;
    arr = s.split(" ");
    return Integer.parseInt(arr[1]);
  }

  //used to get the list of available servers.
  public static ArrayList<Server> getsCapable(DataOutputStream dos, BufferedReader br, int servercount) throws IOException {
    String[] arr;
    ArrayList<Server> aList = new ArrayList<Server>();
    dos.write("OK\n".getBytes());
    dos.flush();
    String recieve = new String();
    int count = 0;
    while (count < servercount) {
      recieve = br.readLine();
      Server server = new Server();
      // comes out like this = joon 0 inactive -1 4 16000 64000 0 0
      //parse the values into pieces. 
      //get the name
      arr = recieve.split(" ");
      server.type = arr[0];
      // get id
      String line = arr[1];
      server.id = Integer.parseInt(line);
      //get status
      line = arr[2];
      server.state = line;
      // get curstarttime
      line = arr[3];
      server.curstarttime= Integer.parseInt(line);
      // get cores
      line = arr[4];
      server.cores = Integer.parseInt(line);
      // get memory
      line = arr[5];
      server.memory = Integer.parseInt(line);
      // get disk
      line = arr[6];
      server.disk = Integer.parseInt(line);
      //get waiting jobs
      line= arr[7];
      server.wjobs = Integer.parseInt(line);
      //get running jobs
      line = arr[8];
      server.rjobs = Integer.parseInt(line);

      aList.add(server);
      count++;
     }
    // return list
    return aList;
  }
  public String determineStrategy(ArrayList<Server> s, String arg, int cores) {
    Server server = null;
    if(arg.equals("fc")) {
      firstCapable fc = new firstCapable(s);
      server = fc.returnServer();
    }
    if(arg.equals("fc")) {
      firstFit ff = new firstFit(s);
      server = ff.returnServer();
    }
    if(arg.equals("bf")) {
      bestFit bf = new bestFit(s, cores);
      server = bf.returnServer();
    }
    if(arg.equals("wf")) {
      worstFit wf = new worstFit(s, cores);
      server = wf.returnServer();
    }
    return server.type + " " + server.id;

  }
}