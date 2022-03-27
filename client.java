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

      while (!recieve.equals("QUIT")) {
        dos.write("REDY\n".getBytes());
        dos.flush();
        // get job values
        String s;
        recieve = br.readLine();
        System.out.println(recieve);
        //sends getscapable
        String job = jobid(recieve); // gets id of job in jobn
        s = "GETS Capable " + jobvalue(recieve) +"\n";
        dos.write(s.getBytes());
        dos.flush();
        //recieves data 3 124
        recieve = br.readLine();
        int howm = howMany(recieve);
        String bestserver = getsCapable(dos, br, howm);
        //once it keeps and finds the best one, it should schd.
        // s = "OK\n";
        // dos.write(s.getBytes());
        dos.flush();
        recieve = br.readLine();
        s = "SCHD " + job + " " + bestserver + "\n";
        dos.write(s.getBytes());
        dos.flush();
        //once i get server info, 
        //loop until i get ".", then schedule to server
      }
      dos.write("QUIT\n".getBytes());
      dos.flush();

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

  public static String jobvalue(String s) {
    String[] a;
    a = s.split(" ", 0);
    return a[4]+" " + a[5]+" " + a[6] + "\n";
  }
  public static String jobid(String s) {
    String[] a;
    a = s.split(" ");
    return a[2];
  }

  public static int howMany(String s) {
    String[] a;
    a = s.split(" ");
    return Integer.parseInt(a[2]);
  }
  public static String getsCapable(DataOutputStream dos, BufferedReader br, int servercount) throws IOException {
    String[] a;
    Server j = new Server();
    ArrayList<Server> aj = new ArrayList<Server>();
    dos.write("OK\n".getBytes());
    dos.flush();
    String recieve = new String();
    boolean barrier = true;
    while(barrier) {
      recieve = br.readLine();
     if (!recieve.equals(".")) {
      System.out.println(recieve);
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
      aj.add(j);
    } else {
      System.out.print(recieve);
      barrier = false;
    }
    dos.write("OK\n".getBytes());
    dos.flush();
  }
    //need to make into separate method after readlingall the lines. 
    Server first = aj.get(0);
    for(int i = 1; i <aj.size(); i++) {
      if(aj.get(i).cores > first.cores) {
        first = aj.get(i);
      }
    }
    // return capable server string

    return first.type + " "+ String.valueOf(first.id);

  }
}