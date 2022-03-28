import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.text.Document;

public class client {
  public static void main(String args[]) {

    try {
      Socket socket = new Socket("127.0.0.1", 50000);
      DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      // DataInputStream dis = new DataInputStream(socket.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      dos.write("HELO\n".getBytes());
      dos.flush();

      String recieve = br.readLine();
      System.out.println(recieve);

      dos.write("AUTH Riku\n".getBytes());
      dos.flush();

      recieve = br.readLine();
      System.out.println(recieve);
      int count = 0;
      while (recieve.compareTo("NONE") != 0) {
        //step 5
        dos.write("REDY\n".getBytes());
        dos.flush();
        // get step 6
        String s;
        recieve = br.readLine();
        if(getcommand(recieve).compareTo("JCPL") == 0) {
          while(getcommand(recieve).compareTo("JCPL") == 0) {
            dos.write("REDY\n".getBytes());
            dos.flush();
            recieve = br.readLine();
          }
        }
        if(recieve.compareTo("NONE") == 0) {
          dos.write("QUIT\n".getBytes());
          dos.flush();
          break;
        }
        System.out.println("job " + recieve + "value");
        String job = jobid(recieve); // gets id of job in jobn
        //step 7
        s = "GETS Capable " + jobvalue(recieve) +"\n";
        dos.write(s.getBytes());
        dos.flush();
        //step 8
        recieve = br.readLine();
        int howm = howMany(recieve);
        System.out.println(howm);
        //step 9
        ArrayList<Server> list = getsCapable(dos, br, howm);
        // Server[] sortedList;
        // sortedList = getBest(list);
        // String bestserver = sortedList[count].state + " " + sortedList[count].id;

        //need to get other servers to work.
        String bestserver = list.get(count).type + " " +  list.get(count).id;
        if(list.get(count).wjobs > 0) {
          count++;
          bestserver = list.get(count).type + " " + list.get(count).id;
        } else {
          count++;
        }
        System.out.println("count = " + count);
        if(count == list.size()-1) {
          count = 0;
        }
        //step 11
        s = "OK\n";
        dos.write(s.getBytes());
        dos.flush();
        recieve = br.readLine();
        //step 7
        s = "SCHD " + job + " " + bestserver + "\n";
        dos.write(s.getBytes());
        dos.flush();
        recieve = br.readLine();
        // s = "OK\n"; //step 6
        // dos.write(s.getBytes());
        // dos.flush();
        // recieve = br.readLine();
      //   recieve = br.readLine();
      //   System.out.println("last msg" + recieve);
      }
      // dos.write("QUIT\n".getBytes());
      // dos.flush();

      String recfinal = br.readLine();
      System.out.println(recfinal);
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

  //used to find value to use in gets command
  public static String jobvalue(String s) {
    String[] a;
    a = s.split(" ", 0);
    return a[4]+" " + a[5]+" " + a[6] + "\n";
  }
  //used to find the id to use to schedule
  public static String jobid(String s) {
    String[] a;
    a = s.split(" ");
    return a[2];
  }
  //used for loop in gets command
  public static int howMany(String s) {
    String[] a;
    a = s.split(" ");
    return Integer.parseInt(a[1]);
  }
  public static ArrayList<Server> getsCapable(DataOutputStream dos, BufferedReader br, int servercount) throws IOException {
    String[] a;
    Server j = new Server();
    ArrayList<Server> aj = new ArrayList<Server>();
    dos.write("OK\n".getBytes());
    dos.flush();
    String recieve = new String();
    int count = 0;
    System.out.println(servercount);
    // boolean barrier = true;
    // while(barrier) {
      //recieve = br.readLine();
    while (count < servercount){
      recieve = br.readLine();
      System.out.println("value" + recieve);
          //joon 0 inactive -1 4 16000 64000 0 0
      a = recieve.split(" ");
      System.out.println(a[0] + a[1]);
      j.type = a[0];
      System.out.println("name = " + j.type);
      // get id
      String l = a[1];
      System.out.println("id = " + l);
      Integer la = Integer.parseInt(l);
      j.id = la;
      // get curstarttime
      l = a[3];
      System.out.println("bootuptime = " + l);
      j.curstarttime= Integer.parseInt(l);
      // get cores
      l = a[4];
      System.out.println("cores = " + l);
      j.cores = Integer.parseInt(l);
      // get memory
      l = a[5];
      System.out.println("memory = " + l);
      j.memory = Integer.parseInt(l);
      // get disk
      l = a[6];
      System.out.println("disk = " + l);
      j.disk = Integer.parseInt(l);
      l = a[7];
      j.wjobs = Integer.parseInt(l);
      l = a[7];
      j.rjobs = Integer.parseInt(l);
      aj.add(j);
      count++;
     }
     for(int i = 0;i < aj.size(); i++) {
      for(int k = 1; k < aj.size(); k++) {
        Server temp = new Server();
        if(aj.get(i).cores < aj.get(k).cores) {
          temp = aj.get(i);
          aj.set(i, aj.get(k));
          aj.set(k, temp);
        }
      }
    }
    // return list
    return aj;

  }
  public static Server[] getBest(ArrayList<Server> a)  {
    Server[] slist = new Server[a.size()];
    for(int i = 0; i<a.size(); i++) {
      slist[i] = a.get(i);
    }
    for(int i = 0;i < slist.length; i++) {
      for(int j = 1; j<slist.length; j++) {
        Server temp;
        if(slist[i].cores < slist[j].cores) {
          temp = slist[i];
          slist[i] = slist[j];
          slist[j] = temp;
        }
      }
    }
    System.out.println("getorder" + slist[0].state);
    return slist;
  }
}