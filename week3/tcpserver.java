import java.io.*;
import java.net.*;

public class tcpserver {
    public static void main(String args[]) {
        try {
            ServerSocket s = new ServerSocket(12345);
            Socket socket = s.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String first = dis.readUTF();
            System.out.println(first);
            if(first.equals("helo")) {
                dos.writeUTF("G'DAY");
                dos.flush();
            }
            String second = dis.readUTF();
            System.out.println(second);
            if(second.equals("BYE")) {
                dos.writeUTF("BYE");
                dos.flush();
                dis.close();
                socket.close();
                s.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
