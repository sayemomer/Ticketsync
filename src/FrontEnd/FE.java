package FrontEnd;

import com.web.frontendController.FEInterface;
import com.web.frontendController.implementation.Frontend;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FE {
    private static final int sequencerPort = 1333;
    //    private static final String sequencerIP = "192.168.2.17";
    private static final String sequencerIP = "localhost";
    //    private static final String RM_Multicast_group_address = "230.1.1.10";
    private static final int FE_SQ_PORT = 1414;
    private static final int FE_PORT = 1999;
    //    private static final int RM_Multicast_Port = 1234;
//    public static String FE_IP_Address = "192.168.2.11";
    public static String FE_IP_Address = "localhost";

    public static void main(String[] args) throws Exception {

        try {

            String serverID = "FE";
            String serverName = "Frontend";
            Frontend service = new Frontend(serverID,serverName);


            System.out.println("Frontend Server Starting.....");

            String serverEndpoint = "http://localhost:8080/frontend";
            Endpoint endpoint = Endpoint.publish(serverEndpoint, service);

            System.out.println(serverName + " Server is Up & Running");

            Runnable task = () -> {
                listenForUDPResponses(service);
            };
            Thread thread = new Thread(task);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static int sendUnicastToSequencer(MyRequest requestFromClient) {
        DatagramSocket aSocket = null;
        String dataFromClient = requestFromClient.toString();
        System.out.println("FE:sendUnicastToSequencer>>>" + dataFromClient);

        System.out.println("FE:sendUnicastToSequencer>>>.........");

        int sequenceID = 0;
        try {
            aSocket = new DatagramSocket(FE_SQ_PORT);
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName(sequencerIP);
            DatagramPacket requestToSequencer = new DatagramPacket(message, dataFromClient.length(), aHost, sequencerPort);

            aSocket.send(requestToSequencer);

            aSocket.setSoTimeout(1000);
            // Set up an UPD packet for recieving
            byte[] buffer = new byte[1000];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            // Try to receive the response from the ping
            aSocket.receive(response);
            String sentence = new String(response.getData(), 0,
                    response.getLength());
            System.out.println("FE:sendUnicastToSequencer/ResponseFromSequencer>>>" + sentence);
            sequenceID = Integer.parseInt(sentence.trim());
            System.out.println("FE:sendUnicastToSequencer/ResponseFromSequencer>>>SequenceID:" + sequenceID);
        } catch (SocketException e) {
            System.out.println("Failed: " + requestFromClient.noRequestSendError());
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed: " + requestFromClient.noRequestSendError());
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return sequenceID;
//        return 0;
    }

    public static void sendMulticastFaultMessageToRms(String errorMessage) {
//        DatagramSocket aSocket = null;
//        try {
//            aSocket = new DatagramSocket();
//            byte[] messages = errorMessage.getBytes();
//            InetAddress aHost = InetAddress.getByName(RM_Multicast_group_address);
//
//            DatagramPacket request = new DatagramPacket(messages, messages.length, aHost, RM_Multicast_Port);
//            System.out.println("FE:sendMulticastFaultMessageToRms>>>" + errorMessage);
//            aSocket.send(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private static void listenForUDPResponses(Frontend service) {

        System.out.println("listening for UDP message from RM through UDP multicast");
//        DatagramSocket aSocket = null;
//        try {
//
////            aSocket = new MulticastSocket(1413);
////            InetAddress[] allAddresses = Inet4Address.getAllByName("SepJ-ROG");
//            InetAddress desiredAddress = InetAddress.getByName(FE_IP_Address);
////            //In order to find the desired Ip to be routed by other modules (WiFi adapter)
////            for (InetAddress address :
////                    allAddresses) {
////                if (address.getHostAddress().startsWith("192.168.2")) {
////                    desiredAddress = address;
////                }
////            }
////            aSocket.joinGroup(InetAddress.getByName("230.1.1.5"));
//            aSocket = new DatagramSocket(FE_PORT, desiredAddress);
//            byte[] buffer = new byte[1000];
//            System.out.println("FE Server Started on " + desiredAddress + ":" + FE_PORT + "............");
//
//            while (true) {
//                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
//                aSocket.receive(response);
//                String sentence = new String(response.getData(), 0,
//                        response.getLength()).trim();
//                System.out.println("FE:Response received from Rm>>>" + sentence);
//                RmResponse rmResponse = new RmResponse(sentence);
////                String[] parts = sentence.split(";");
//
//                System.out.println("Adding response to FrontEndImplementation:");
//                servant.addReceivedResponse(rmResponse);
////                DatagramPacket reply = new DatagramPacket(response.getData(), response.getLength(), response.getAddress(),
////                        response.getPort());
////                aSocket.send(reply);
//            }
//
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("IO: " + e.getMessage());
//        } finally {
////            if (aSocket != null)
////                aSocket.close();
//        }
    }
}