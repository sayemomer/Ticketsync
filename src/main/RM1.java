package main;

//import Replica1.DataBase.Message;
//import Replica1.Server.Montreal;
//import Replica1.Server.Quebec;
//import Replica1.Server.Sherbrooke;
//import Replica1.ServerInterface.EventManagementInterface;

import main.RMmodel.Message;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RM1 {
    private static final String Bug_ID = "MTLM8888";
    private static final String Crash_ID = "MTLM9999";
    public static int lastSequenceID = 1;
    public static int bug_counter = 0;
    public static ConcurrentHashMap<Integer, Message> message_list = new ConcurrentHashMap<>();
    public static Queue<Message> message_q = new ConcurrentLinkedQueue<Message>();
    private static boolean serversFlag = true;
    private static boolean BugFlag = true;

    public static void main(String[] args) throws Exception {
        Run();
    }

    private static void Run() throws Exception {
        Runnable task = () -> {
            try {
                receive();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private static void receive() throws Exception {
        MulticastSocket socket = null;
        try {
            NetworkInterface ni = NetworkInterface.getByName("en0");



//            NetworkInterface ni = NetworkInterface.getByName("en0");
//
//            InetAddress address = InetAddress.getByName("230.1.1.10");
//
//            aSocket = new MulticastSocket(4446);
//            aSocket.setInterface(ni.getInetAddresses().nextElement());
//            aSocket.joinGroup(address);

            socket = new MulticastSocket(1234);
            socket.setInterface(ni.getInetAddresses().nextElement());
            socket.joinGroup(InetAddress.getByName("230.1.1.10"));

            byte[] buffer = new byte[1000];
            System.out.println("RM1 UDP Server Started(port=1234)............");

            //Run thread for executing all messages in queue
            Runnable task = () -> {
                try {
                    executeAllRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(task);
            thread.start();

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String data = new String(request.getData(), 0, request.getLength());
                String[] parts = data.split(";");

                /*
                Message Types:
                    00- Simple message
                    01- Sync request between the RMs
                    02- Initialing RM
                    11-Rm1 has bug
                    12-Rm2 has bug
                    13-Rm3 has bug
                    21-Rm1 is down
                    22-Rm2 is down
                    23-Rm3 is down
                */
                System.out.println("RM1 recieved message. Detail:" + data);
                if (parts[2].equalsIgnoreCase("00")) {
                    Message message = message_obj_create(data);
                    if (!message.userID.equalsIgnoreCase(Crash_ID)) {
                        Message message_To_RMs = message_obj_create(data);
                        message_To_RMs.MessageType = "01";
                        send_multicast_toRM(message_To_RMs);
                        if (message.sequenceId - lastSequenceID > 1) {
                            Message initial_message = new Message(0, "Null", "02", Integer.toString(lastSequenceID), Integer.toString(message.sequenceId), "RM1", "Null", "Null", "Null", 0);
                            System.out.println("RM1 send request to update its message list. from:" + lastSequenceID + "To:" + message.sequenceId);
                            // Request all RMs to send back list of messages
                            send_multicast_toRM(initial_message);
                        }
                        System.out.println("is adding queue:" + message + "|| lastSequence>>>" + lastSequenceID);
                        message_q.add(message);
                        message_list.put(message.sequenceId, message);
                    }
                } else if (parts[2].equalsIgnoreCase("01")) {
                    Message message = message_obj_create(data);
                    if (!message_list.contains(message.sequenceId))
                        message_list.put(message.sequenceId, message);
                } else if (parts[2].equalsIgnoreCase("02")) {
                    initial_send_list(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
                } else if (parts[2].equalsIgnoreCase("03") && parts[5].equalsIgnoreCase("RM1")) {
                    update_message_list(parts[1]);
                } else if (parts[2].equalsIgnoreCase("11")) {
                    Message message = message_obj_create(data);
                    BugFlag = false;
                    System.out.println("Rm1 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("12")) {
                    Message message = message_obj_create(data);
                    System.out.println("Rm1 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("13")) {
                    Message message = message_obj_create(data);
                    System.out.println("Rm1 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("21")) {
                    Runnable crash_task = () -> {
                        try {

                            System.out.println("RM1 is going to shutdown");
//                            //suspend the execution of messages untill all servers are up. (serversFlag=false)
//                            serversFlag = false;
//                            //reboot Monteal Server
//                            Registry montreal_registry = LocateRegistry.getRegistry(9992);
//                            EventManagementInterface montreal_obj = (EventManagementInterface) montreal_registry.lookup("ServerClass");
//                            montreal_obj.shutDown();
//                            System.out.println("RM1 shutdown Montreal Server");
//
//                            //reboot Quebec Server
//                            Registry quebec_registry = LocateRegistry.getRegistry(9991);
//                            EventManagementInterface quebec_obj = (EventManagementInterface) quebec_registry.lookup("ServerClass");
//                            quebec_obj.shutDown();
//                            System.out.println("RM1 shutdown Quebec Server");
//
//                            //reboot Sherbrooke Server
//                            Registry sherbrook_registry = LocateRegistry.getRegistry(9993);
//                            EventManagementInterface sherbrook_obj = (EventManagementInterface) sherbrook_registry.lookup("ServerClass");
//                            sherbrook_obj.shutDown();
//                            System.out.println("RM1 shutdown Sherbrooke Server");
//
//                            //running all servers
//                            Montreal.main(new String[0]);
//                            Thread.sleep(500);
//                            Quebec.main(new String[0]);
//                            Thread.sleep(500);
//                            Sherbrooke.main(new String[0]);
//
//                            //wait untill are servers are up
//                            Thread.sleep(5000);
//
//                            System.out.println("RM1 is reloading servers hashmap");
//                            reloadServers();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                    Thread handleThread = new Thread(crash_task);
                    handleThread.start();
                    handleThread.join();
                    System.out.println("RM1 handled the crash!");
                    serversFlag = true;
                }
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private static Message message_obj_create(String data) {
        String[] parts = data.split(";");
        int sequenceId = Integer.parseInt(parts[0]);
        String FrontIpAddress = parts[1];
        String MessageType = parts[2];
        String Function = parts[3];
        String userID = parts[4];
        String newEventID = parts[5];
        String newEventType = parts[6];
        String oldEventID = parts[7];
        String oldEventType = parts[8];
        int bookingCapacity = Integer.parseInt(parts[9]);
        Message message = new Message(sequenceId, FrontIpAddress, MessageType, Function, userID, newEventID, newEventType, oldEventID, oldEventType, bookingCapacity);
        return message;
    }

    // Create a list of messsages, seperating them with @ and send it back to RM
    private static void initial_send_list(Integer begin, Integer end, String RmNumber) {
        String list = "";
        for (ConcurrentHashMap.Entry<Integer, Message> entry : message_list.entrySet()) {
            if (entry.getValue().sequenceId > begin && entry.getValue().sequenceId < end) {
                list += entry.getValue().toString() + "@";
            }
        }
        // Remove the last @ character
        if (list.endsWith("@"))
            list.substring(list.length() - 1);
        Message message = new Message(0, list, "03", begin.toString(), end.toString(), RmNumber, "Null", "Null", "Null", 0);
        System.out.println("RM1 sending its list of messages for initialization. list of messages:" + list);
        send_multicast_toRM(message);
    }

    //update the hasmap and and new data to queue to be execited
    private static void update_message_list(String data) {
        String[] parts = data.split("@");
        for (int i = 0; i < parts.length; ++i) {
            Message message = message_obj_create(parts[i]);
            //we get the list from 2 other RMs and will ensure that there will be no duplication
            if (!message_list.containsKey(message.sequenceId)) {
                System.out.println("RM1 update its message list" + message);
                message_q.add(message);
                message_list.put(message.sequenceId, message);
            }
        }
    }

    private static void send_multicast_toRM(Message message) {
        int port = 1234;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] data = message.toString().getBytes();
            InetAddress aHost = InetAddress.getByName("230.1.1.10");

            DatagramPacket request = new DatagramPacket(data, data.length, aHost, port);
            socket.send(request);
            System.out.println("Message multicasted from RM1 to other RMs. Detail:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Execute all request from the lastSequenceID, send the response back to Front and update the counter(lastSequenceID)
    private static void executeAllRequests() throws Exception {
        System.out.println("before while true");
        while (true) {
            synchronized (RM1.class) {
                Iterator<Message> itr = message_q.iterator();
                while (itr.hasNext()) {
                    Message data = itr.next();
                    //when the servers are down serversFlag is False therefore, no execution untill all servers are up.
                    if (data.sequenceId == lastSequenceID && serversFlag) {
                        if (data.userID.equalsIgnoreCase(Bug_ID) && BugFlag == true) {
//                            if (bug_counter == 0)
                            System.out.println("RM1 is executing message request. Detail:" + data);
                            requestToServers(data);
                            Message bug_message = new Message(data.sequenceId, "Null", "RM1",
                                    data.Function, data.userID, data.newEventID,
                                    data.newEventType, data.oldEventID,
                                    data.oldEventType, data.bookingCapacity);
//                            bug_counter += 1;
                            lastSequenceID += 1;
                            messsageToFront(bug_message.toString(), data.FrontIpAddress);
                            message_q.poll();
                        } else {
                            System.out.println("RM1 is executing message request. Detail:" + data);
                            String response = requestToServers(data);
                            Message message = new Message(data.sequenceId, response, "RM1",
                                    data.Function, data.userID, data.newEventID,
                                    data.newEventType, data.oldEventID,
                                    data.oldEventType, data.bookingCapacity);
                            lastSequenceID += 1;
                            messsageToFront(message.toString(), data.FrontIpAddress);
                            message_q.poll();
                        }
//                    message_q.remove(data);
//                    itr.remove();
                    }
                }
//                message_q.clear();
            }
        }
    }

    //Send RMI request to server
    private static String requestToServers(Message input) throws Exception {
        String serverName = input.userID.substring(0, 3);
        System.out.println(input.userID.substring(0, 3));
        int port = 0;

        if(serverName.equalsIgnoreCase("ATW")) {
            port = 8888;
        } else if(serverName.equalsIgnoreCase("OUT")) {
            port = 7777;
        } else if(serverName.equalsIgnoreCase("VER")) {
            port = 6666;
        }

        DatagramSocket aSocket = null;

        try {
            aSocket = new DatagramSocket();
            byte[] m = input.toString().getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
            aSocket.send(request);
            System.out.println("RM1 sending request to server:" + ' ' + port);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            String response = new String(reply.getData());
            System.out.println("RM1 received response from server:" + response);
            return response;
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return "Null response from server" + input.userID.substring(0, 3);
    }


    private static int serverPort(String input) {
        String branch = input.substring(0, 3);
        int portNumber = -1;

        if (branch.equalsIgnoreCase("que"))
            portNumber = 9991;
        else if (branch.equalsIgnoreCase("mtl"))
            portNumber = 9992;
        else if (branch.equalsIgnoreCase("she"))
            portNumber = 9993;

        return portNumber;
    }

    public static void messsageToFront(String message, String FrontIpAddress) {
        System.out.println("Message to front:" + message);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4321);
            byte[] bytes = message.getBytes();
            InetAddress aHost = InetAddress.getByName(FrontIpAddress);

            System.out.println(aHost);
            DatagramPacket request = new DatagramPacket(bytes, bytes.length, aHost, 1999);
            socket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

    public static void reloadServers() throws Exception {
        for (ConcurrentHashMap.Entry<Integer, Message> entry : message_list.entrySet()) {
            System.out.println("Recovery Mood-RM1 is executing message request. Detail:" + entry.getValue().toString());
            requestToServers(entry.getValue());
            if (entry.getValue().sequenceId >= lastSequenceID)
                lastSequenceID = entry.getValue().sequenceId + 1;
        }
        message_q.clear();
    }
}