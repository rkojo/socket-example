import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;

public class client {
  static boolean testing = true;
  public static void main(String args[]) {
    Boolean first = true;
    serverArray serverArr = new serverArray();


    
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
        // jcpl tells status of job, so it should loop until all jobs are done.
        if (getcommand(recieve).compareTo("JCPL") == 0) {
          while (getcommand(recieve).compareTo("JCPL") == 0) {
            jobArray job = new jobArray();
            for (int i = 0; i < serverArr.servers.size(); i++) {
                  String lstj = "LSTJ " + serverArr.servers.get(i).type + " " + serverArr.servers.get(i).id + "\n";
                  dos.write(lstj.getBytes());
                  dos.flush();
                  recieve = br.readLine();
                  int count = 0;
                  int amount = howMany(recieve);
                  dos.write("OK\n".getBytes());
                  dos.flush();
                  if(amount > count) {
                  while (amount > count) {
                    // add amount to waiting jobs
                    recieve = br.readLine();
                    // add to list
                    jobs tempjob = readJobs(recieve, serverArr.servers.get(i).type, serverArr.servers.get(i).id);
                    if(tempjob.jobState == 1) {
                      job.add(tempjob);
                    }
                    count++;
                  }
                  dos.write("OK\n".getBytes());
                  dos.flush();
                  recieve = br.readLine();
                } else {
                  recieve  = br.readLine();
                }
                
                }
                job.list.sort(new jobCompare());
                Boolean works = true;
                  for (int i = 0; i < job.list.size(); i++) {
                    if( works == true) {
                    if(job.list.get(i).jobState == 1) {
                    String getsavailable = ("GETS Capable " + job.list.get(i).core + " " + job.list.get(i).memory + " " + job.list.get(i).disk +"\n");
                    dos.write(getsavailable.getBytes());
                    dos.flush();
                    recieve = br.readLine();
                    String[] errorCheck = recieve.split(" ");
                    if(errorCheck[0].compareTo("ERR:") != 0) {
                    int howmany = howMany(recieve);
                    ArrayList<Server> available = getsCapable(dos, br, howmany);
                    dos.write("OK\n".getBytes());
                    dos.flush();
                    recieve = br.readLine();
                    Server bestServer = available.get(0);
                  for(int k = 1; k<available.size(); k++) {
                    if( available.get(k).wjobs < bestServer.wjobs && job.list.get(i).core <= available.get(k).cores) {
                      bestServer = available.get(k);
                    }
                  }
                  if (job.list.get(i).jobState == 1) {
                    String migrate = ("MIGJ " + job.list.get(i).jobID + " " + job.list.get(i).srcServer + " "
                        + job.list.get(i).secServerid + " " + bestServer.type + " " + bestServer.id
                        + "\n");
                    
                  
                    dos.write(migrate.getBytes());
                    dos.flush();
                    recieve = br.readLine();
                    System.out.println(recieve);
                    break;
                  }
                    
                    } else {
                      dos.write("REDY\n".getBytes());
                      dos.flush();
                      recieve = br.readLine();
                      works = false;
                }   
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
          String bestserver;
          if (args.length == 0) {
            bestserver = customStrategy(list, jobcore);
          } else {
            bestserver = determineStrategy(list, serverArr.servers, args[0], jobcore);
          }
          // step 7 - job scheduling
          s = "SCHD " + jobToSend + " " + bestserver + "\n";
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

    }catch(

  IOException e)
  {
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

  public static String determineStrategy(ArrayList<Server> s, ArrayList<Server> sa, String arg, int cores) {
    Server server = null;
    if (arg.equals("fc")) {
      // System.out.println("enters fc");
      firstCapable fc = new firstCapable(s);
      server = fc.returnServer();
    }
    if (arg.equals("ff")) {
      // System.out.println("enters ff");
      firstFit ff = new firstFit(s);
      server = ff.returnServer();
    }
    if (arg.equals("bf")) {
      // System.out.println("enters bf");
      bestFit bf = new bestFit(s, sa, cores);
      server = bf.returnServer();
    }
    if (arg.equals("wf")) {
      // System.out.println("enters wf");
      worstFit wf = new worstFit(s, sa, cores);
      server = wf.returnServer();
    }
    return server.type + " " + server.id;
  }

  public static String customStrategy(ArrayList<Server> s, int cores) {
    Server server = null;
    customfit cf = new customfit(s, cores);
    server = cf.returnServer();
    return server.type + " " + server.id;
  }

  public static jobs readJobs(String recieve, String server, int id) {
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
    temp.secServerid = id;
    return temp;
  }

  public static int howManyJobs(String s) {
    String[] arr;
    arr = s.split(" ");
    return Integer.parseInt(arr[0]);
  }
}