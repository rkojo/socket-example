import java.io.*;
import java.net.*;

public class tcpclient {
    public static void main(String args[]) {
        try {
            Socket socket = new Socket("127.0.0.1", 12345);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            dos.writeUTF("helo");
            dos.flush();
            String recieve = dis.readUTF();
            System.out.println(recieve);
            if(recieve.equals("G'DAY")) {
                dos.writeUTF("BYE");
                dos.flush();
            }
            String recfinal = dis.readUTF();
            System.out.println(recfinal);
            if(recfinal.equals("BYE")) {
                dos.close();
                socket.close();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
