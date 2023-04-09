package Assignment3;

import java.io.IOException;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Scanner;

public class C1 {
    public static void main(String[] args) throws IOException {


        DatagramSocket ds = new DatagramSocket();
        String sentence = "4"+" "+"121.0.0.1"+" "+"00"+" "+"ExchnageMovie"+" "+"ATWC2211"+ " "+  "ATWE230318" + " "+ "Titanic"+" "+  "ATWA230318" + " "+ "Titanic"+" "+"100";
        System.out.println(sentence);
        byte[] b1 = (sentence +"").getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        int port = 1234;

        DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
        ds.send(dp);

        byte[] b2 = new byte[1024];
        DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
        ds.receive(dp1);

        String receive = new String(dp1.getData(), 0, dp1.getLength());
        System.out.println(receive);

    }
}
