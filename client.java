import java.io.*;
import java.net.*;

public class client {
    public static void main(String args[]) {
     DatagramSocket socket = null;
    try {
        socket = new DatagramSocket();
        InetAddress add = InetAddress.getByName(args[1]);
        int port = 6248;
        byte[] s = args[0].getBytes();
        byte[] buffer = new byte[1000];
        //send helo
        DatagramPacket helo = new DatagramPacket(s, s.length, add, port);
        socket.send(helo);
        //recieve data
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        socket.receive(reply);
        String rep = new String(reply.getData());
        System.out.println("reply: " + rep);
            String b = "bye";
            DatagramPacket again = new DatagramPacket(b.getBytes(), b.length(), add, port);
            socket.send(again);
        byte[] b2 = new byte[1000];
        DatagramPacket total = new DatagramPacket(b2,b2.length);
        socket.receive(total);
        String t = new String(total.getData());
        System.out.println( "reply 2: " + t);
    } catch(SocketException e ) {
        System.out.println("socket: " + e.getMessage());
    } catch(IOException e ) {
        System.out.println("io:" + e.getMessage());
    } finally {
        if(socket != null) {
            socket.close();
            System.out.println("closed socket");
        }
        }
    }
}
