import java.io.*;
import java.net.*;

public class server {
  public static void main(String[] args) {
    DatagramSocket socket1 = null;
    try {
      socket1 = new DatagramSocket(6248);
      byte[] buffer = new byte[1000];
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
      socket1.receive(dp);
      String first = new String(dp.getData());
      System.out.println("recieve: " + first);
      //when it recieves helo
      String s = "G'DAY";
      DatagramPacket reply = new DatagramPacket(s.getBytes(), s.length(), dp.getAddress(), dp.getPort());
      socket1.send(reply);
      //recieve the data
      DatagramPacket rec = new DatagramPacket(buffer, buffer.length);
      socket1.receive(rec);
      String second = new String(rec.getData());
      System.out.println("reply: " + second);
      //when it gets bye
        String s2 = "BYE";
        DatagramPacket send2 = new DatagramPacket(s2.getBytes(), s2.length(), dp.getAddress(), dp.getPort());
        socket1.send(send2);
      DatagramPacket end = new DatagramPacket(buffer, buffer.length);
      socket1.receive(end);
      System.out.print(new String(end.getData()));
    } catch (SocketException e) {
      System.out.println("socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } finally {
      if(socket1 !=null) {
      socket1.close();
      System.out.println("closed socket");
      }
    }
  }

}
