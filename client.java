import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class client {
  public static void main(String args[]) {
    Boolean first = true;
    serverArray serverArr = new serverArray();
    customfit cf = new customfit();

    // used to store the values of the biggest servers and keeps them even when
    // looping.
    try {
      Socket socket = new Socket("127.0.0.1", 50000);
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      // DataInputStream dis = new DataInputStream(socket.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      dos.write("HELO\n".getBytes());
      dos.flush();

      String recieve = br.readLine();
      // authentication
      dos.write("AUTH riku\n".getBytes());
      dos.flush();

      recieve = br.readLine();
      // if none, the whole thing is completed.
      while (recieve.compareTo("NONE") != 0) {
        // step 5
        dos.write("REDY\n".getBytes());
        dos.flush();
        // get step 6
        String s;
        recieve = br.readLine();
        // System.out.println(recieve);
        // jcpl tells status of job, so it should loop until all jobs are done.
        if (getcommand(recieve).compareTo("JCPL") == 0) {
          while (getcommand(recieve).compareTo("JCPL") == 0) {
            jobArray job = new jobArray();
            for (int i = 0; i < serverArr.servers.size(); i++) {
              // get status of job in servers
              String lstj = "LSTJ " + serverArr.servers.get(i).type + " " + serverArr.servers.get(i).id + "\n";
              dos.write(lstj.getBytes());
              dos.flush();
              recieve = br.readLine();
              int count = 0;
              // get amount in response (DATA)
              int amount = howMany(recieve);
              dos.write("OK\n".getBytes());
              dos.flush();
              if (amount > count) {
                while (amount > count) {
                  // add amount to waiting jobs
                  recieve = br.readLine();
                  // add to list
                  jobs tempjob = readJobs(recieve, serverArr.servers.get(i));
                  if (tempjob.jobState == 1) {
                    job.add(tempjob);
                  }
                  count++;
                }
                dos.write("OK\n".getBytes());
                dos.flush();
                recieve = br.readLine();
              } else {
                // if no data
                recieve = br.readLine();
              }

            }
            job.list.sort(new jobCompare());
            for (int i = 0; i < job.list.size(); i++) {
              if (job.list.get(i).jobState == 1) {
                // get capable severs for jobs.
                String getsavailable = ("GETS Capable " + job.list.get(i).core + " " + job.list.get(i).memory + " "
                    + job.list.get(i).disk + "\n");
                dos.write(getsavailable.getBytes());
                dos.flush();
                recieve = br.readLine();
                int howmany = howMany(recieve);
                ArrayList<Server> available = getsCapable(dos, br, howmany);
                dos.write("OK\n".getBytes());
                dos.flush();
                recieve = br.readLine();
                available.sort(new serverCompare());
                // get the best server
                Server bestServer = cf.migrateServer(available, job.list.get(i).srcServer);
                // if the server is to change.
                if (bestServer != job.list.get(i).srcServer) {
                  String migrate = ("MIGJ " + job.list.get(i).jobID + " " + job.list.get(i).srcServer.type + " "
                      + job.list.get(i).srcServer.id + " " + bestServer.type + " " + bestServer.id
                      + "\n");
                  dos.write(migrate.getBytes());
                  dos.flush();
                  recieve = br.readLine();
                }
              }

            }
          }
        }
        // if the value is none, it quits and breaks out of loop.
        if (getcommand(recieve).compareTo("NONE") == 0) {
          dos.write("QUIT\n".getBytes());
          dos.flush();
          break;
        }
        if (getcommand(recieve).compareTo("JOBN") == 0) {
          String jobToSend = jobid(recieve); // gets id of job in jobn

          // step 7
          s = "GETS Capable " + jobvalue(recieve) + "\n";
          int jobcore = jobCores(recieve);
          dos.write(s.getBytes());
          dos.flush();
          // gets the amount of servers
          recieve = br.readLine();
          int howm = howMany(recieve);
          // step 9 - gets the list of servers.
          ArrayList<Server> list = getsCapable(dos, br, howm);
          if (first == true) {
            serverArr.servers = list;
            first = false;
          }
          s = "OK\n";
          dos.write(s.getBytes());
          dos.flush();
          recieve = br.readLine();
          // stores the best servers only.
          // the best server possible and the id of the server.
          Server customserver = cf.returnServer(list, jobcore);
          String toSend = customserver.type + " " + customserver.id;

          // step 7 - job scheduling
          s = "SCHD " + jobToSend + " " + toSend + "\n";
          dos.write(s.getBytes());
          dos.flush();
          recieve = br.readLine();
        }
      }
      // reads final value.
      String recfinal = br.readLine();
      if (recfinal.equals("QUIT")) {
        dos.close();
        socket.close();
      }

    } catch (

    IOException e) {
      e.printStackTrace();
    }

  }

  // used to return to detect jcpl
  public static String getcommand(String s) {
    String[] arr;
    arr = s.split(" ");
    return arr[0];
  }

  // used to find job requirement for core, disk and memory to use in GETS capable
  public static String jobvalue(String s) {
    String[] arr;
    arr = s.split(" ", 0);
    return arr[4] + " " + arr[5] + " " + arr[6] + "\n";
  }

  // get the amount of cores required for the job.
  public static int jobCores(String s) {
    String[] arr;
    arr = s.split(" ", 0);
    return Integer.parseInt(arr[4]);
  }

  // used to find the id of the job to use to schedule jobs.
  public static String jobid(String s) {
    String[] arr;
    arr = s.split(" ");
    return arr[2];
  }

  // sees how many capable servers to use in the getscapable loop.
  public static int howMany(String s) {
    String[] arr;
    arr = s.split(" ");
    return Integer.parseInt(arr[1]);
  }

  // used to get the list of available servers.
  public static ArrayList<Server> getsCapable(DataOutputStream dos, BufferedReader br, int servercount)
      throws IOException {
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
      // parse the values into pieces.
      // get the name
      arr = recieve.split(" ");
      server.type = arr[0];
      // get id
      String line = arr[1];
      server.id = Integer.parseInt(line);
      // get status
      line = arr[2];
      server.state = line;
      // get curstarttime
      line = arr[3];
      server.curstarttime = Integer.parseInt(line);
      // get cores
      line = arr[4];
      server.cores = Integer.parseInt(line);
      // get memory
      line = arr[5];
      server.memory = Integer.parseInt(line);
      // get disk
      line = arr[6];
      server.disk = Integer.parseInt(line);
      // get waiting jobs
      line = arr[7];
      server.wjobs = Integer.parseInt(line);
      // get running jobs
      line = arr[8];
      server.rjobs = Integer.parseInt(line);

      aList.add(server);
      count++;
    }
    // return list
    return aList;
  }

  public static jobs readJobs(String recieve, Server server) {
    // read the jobs and the server it is from
    String[] arr = recieve.split(" ");
    jobs temp = new jobs();
    temp.jobID = Integer.parseInt(arr[0]);
    temp.jobState = Integer.parseInt(arr[1]);
    temp.submitTime = Integer.parseInt(arr[2]);
    temp.startTime = Integer.parseInt(arr[3]);
    temp.estRunttime = Integer.parseInt(arr[4]);
    temp.core = Integer.parseInt(arr[5]);
    temp.memory = Integer.parseInt(arr[6]);
    temp.disk = Integer.parseInt(arr[7]);
    temp.srcServer = server;

    return temp;
  }
}