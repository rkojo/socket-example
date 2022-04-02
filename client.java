import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class client {
  public static void main(String args[]) {
    //used to store the values of the biggest servers and keeps them even when looping. 
    serverArray serverArr = new serverArray();
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
      int count = 0;
      //used to see if the loop has not looped before. used to store values into serverArray
      boolean toStore = true;
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

        //on the first loop, it saves the servers with the biggest values.
        //this only needs to happen once as it keeps the server data at the end in serverArr
        if(toStore == true) {
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
          serverArr.servers = bigAmount(list);
        }
        toStore = false;
        //the best server possible and the id of the server. 
        String bestserver = serverArr.returnValue(count) + " " +  serverArr.returnInt(count);
        count++;
        //used to ensure that it server id does not go over. If there are three servers and 9 jobs, this will
        //happen three times.
        if(count >= serverArr.size()) {
          count = 0;
        }
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
    String[] a;
    a = s.split(" ");
    return a[0];
  }

  //used to find job requirement for core, disk and memory to use in GETS capable 
  public static String jobvalue(String s) {
    String[] a;
    a = s.split(" ", 0);
    return a[4]+" " + a[5]+" " + a[6] + "\n";
  }
  //used to find the id of the job to use to schedule jobs. 
  public static String jobid(String s) {
    String[] a;
    a = s.split(" ");
    return a[2];
  }

  //sees how many capable servers to use in the getscapable loop. 
  public static int howMany(String s) {
    String[] a;
    a = s.split(" ");
    return Integer.parseInt(a[1]);
  }

  //used to get the list of available servers.
  public static ArrayList<Server> getsCapable(DataOutputStream dos, BufferedReader br, int servercount) throws IOException {
    String[] a;
    ArrayList<Server> aj = new ArrayList<Server>();
    dos.write("OK\n".getBytes());
    dos.flush();
    String recieve = new String();
    int count = 0;
    while (count < servercount) {
      recieve = br.readLine();
      Server j = new Server();
      // comes out like this = joon 0 inactive -1 4 16000 64000 0 0
      //parse the values into pieces. 
      //get the name
      a = recieve.split(" ");
      j.type = a[0];
      // get id
      String l = a[1];
      Integer la = Integer.parseInt(l);
      j.id = la;
      // get curstarttime
      l = a[3];
      j.curstarttime= Integer.parseInt(l);
      // get cores
      l = a[4];
      j.cores = Integer.parseInt(l);
      // get memory
      l = a[5];
      j.memory = Integer.parseInt(l);
      // get disk
      l = a[6];
      j.disk = Integer.parseInt(l);
      l = a[7];
      //get waiting jobs
      j.wjobs = Integer.parseInt(l);
      l = a[7];
      //running jobs
      j.rjobs = Integer.parseInt(l);
      aj.add(j);
      count++;
     }
    // return list
    return aj;

  }
  //find largest amount of servers for LRR
  public static ArrayList<Server> bigAmount(ArrayList<Server> a) {
    //get the biggest core value and stores into temp. Should only be the first value as it is not <=
    Server temp = a.get(0);
    for(int i = 1; i<a.size(); i++) {
      if(temp.cores < a.get(i).cores) {
        temp = a.get(i);
      }
    }
    //stores the values of the same type server into list. 
    //Uses type instead of cores as cores would get multiple servers with the same core count. 
    ArrayList<Server> newaj = new ArrayList<Server>();
    for(int i = 0; i<a.size(); i++) {
      if(temp.type.equals(a.get(i).type)) {
        newaj.add(a.get(i));
      }
    }
    return newaj;
  }
}