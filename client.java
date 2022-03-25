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

      dos.write("REDY\n".getBytes());
      dos.flush();
      while (!recieve.equals("QUIT")) {
        // get job values
        String s;
        recieve = br.readLine();
        System.out.println(recieve);
        s = "GETS capable" + jobvalue(recieve) + "\n";
        dos.write(s.getBytes());
        dos.flush();
        recieve = br.readLine();
        s = getsCapable(recieve);

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
    return a[4] + a[5] + a[6];
  }

  public static String getsCapable(String s) {
    String[] a;
    Server j;
    ArrayList<Server> aj = new ArrayList<Server>();
    while (!s.equals(".")) {
      a = s.split(" ", 0);
      a[0] = j.type;
      // get limit
      String l = a[1];
      Integer la = Integer.parseInt(l);
      j.limit = la;
      // get bootuptime
      l = a[2];
      j.bootupTime = Integer.parseInt(l);
      // get hourlyRate (double)
      l = a[3];
      Double da = Double.parseDouble(l);
      j.hourlyRate = da;
      // get cores
      l = a[4];
      j.cores = Integer.parseInt(l);
      // get memory
      l = a[5];
      j.memory = Integer.parseInt(l);
      // get disk
      l = a[6];
      j.disk = Integer.parseInt(l);
      aj.add(j);
    }
    // return capable server string

    return;

  }
}