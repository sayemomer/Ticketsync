package RM3;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static RM3.RemoteImplementation1.movieDatabase;

public class OutremontServer {
    public static void main(String[] args) throws IOException {

        RemoteImplementation1 obj = new RemoteImplementation1("Outremont");
        DatagramSocket asocket = new DatagramSocket(6013);
        byte[] buffer = new byte[1024];
        DatagramPacket apacket = new DatagramPacket(buffer, buffer.length);
        System.out.println("Outremot server is  ready and waiting ...");


        RemoteImplementation1 sr = new RemoteImplementation1("Outremont");
        Runnable task = () -> {
            try {
                UDPServer(sr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread th = new Thread(task);
        th.start();


        while (true) {
            asocket.receive(apacket);
            String sentence = new String(apacket.getData(), 0, apacket.getLength());
            System.out.println("Message Received from RM " + sentence);
            String[] parts = sentence.split(";");
            String functions = parts[3];
            String response = null;
            if(functions.equalsIgnoreCase("addMovieSlots")){
                String movieName = parts[6];
                String movieID = parts[5];
                String bookCapacity = parts[9];

                System.out.println("movieName: " + movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("bookingCapacity e: " + bookCapacity);

                int BookingCapacity = Integer.parseInt(bookCapacity);
                response = obj.addMovieSlots(movieName, movieID, BookingCapacity);
            } else if(functions.equalsIgnoreCase("removeMovieSlots")){
                String movieId = parts[5];
                String movieName = parts[6];
                response = obj.removeMovieSlots(movieId, movieName);
            } else if(functions.equalsIgnoreCase("listMovieShowsAvailability")){
                String movieName = parts[6];
                response = obj.listMovieShowsAvailability(movieName);
            } else if (functions.equalsIgnoreCase("bookMovieTickets")){
                String customerID = parts[4];
                String movieName = parts[6];
                String movieID = parts[5];
                String tickets = parts[9];

                System.out.println("customerID: "+customerID);
                System.out.println("movieName: " + movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("number of tickets: " + tickets);

                int number_of_tickets = Integer.parseInt(tickets);
                response = obj.bookMovieTickets(customerID, movieID,movieName, number_of_tickets);
            }else if(functions.equalsIgnoreCase("getBookingSchedule")){
                String customerID = parts[4];
                System.out.println("customerID : " + customerID);
                response =  obj.getBookingSchedules(customerID);

            }else if(functions.equalsIgnoreCase("cancelMovieTickets")) {
                String customerID = parts[4];
                String movieID = parts[5];
                String movieName = parts[6];
                String numOfTickets = parts[9];
                System.out.println("customerID: " + customerID);
                System.out.println("movieID: " + movieID);
                System.out.println("movieName: " + movieName);
                System.out.println("numberOfTickets: " + numOfTickets);

                int numberOfTickets = Integer.parseInt(numOfTickets);

                response = obj.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
            }else if(functions.equalsIgnoreCase("ExchnageMovie")) {
                String customerID = parts[4];
                String movieID = parts[7];
                String new_movieName = parts[6];
                String new_movieID = parts[5];
                String tickets = parts[9];

                System.out.println("customerID: "+customerID);
                System.out.println("movieName: " + new_movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("number of tickets: " + tickets);

                int number_of_tickets = Integer.parseInt(tickets);
                response = obj.exchangeTickets(customerID,movieID, new_movieID, new_movieName, number_of_tickets);
            }

            System.out.println(response);
            byte[] b20 = (response + "").getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            DatagramPacket dp5 = new DatagramPacket(b20, b20.length, ia, apacket.getPort());
            asocket.send(dp5);


        }
    }

    public static void UDPServer(RemoteImplementation1 sr) throws IOException {
        try {

            DatagramSocket ds = new DatagramSocket(6124);

            byte[] b1 = new byte[1024];
            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            System.out.println("UDP server is started...");
            while (true) {
                ds.receive(dp);
                String sentence = new String(dp.getData(), 0, dp.getLength());
                System.out.println("Received sentence is : " + sentence);
                String[] data = sentence.split(" ");
                String funct = data[0];

                String res = null;
                if (funct.substring(0, 16).equals("bookMovieTickets")) {
                    String customerID = data[1];
                    String movieID = data[2];
                    String movieName = data[3];
                    String numOfTickets = data[4];
                    System.out.println("customerID: " + customerID);
                    System.out.println("movieID: " + movieID);
                    System.out.println("movieName: " + movieName);
                    System.out.println("numberOfTickets: " + numOfTickets);

                    int numberOfTickets = Integer.parseInt(numOfTickets);
                    if (movieDatabase.get(movieName).get(movieID).Tickets.containsKey(customerID)) {
                        res = "Can't Book";
                    } else {
                        res = sr.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
                    }
                } else if (funct.length() >= 18 && funct.substring(0, 18).equals("getBookingSchedule")) {
                    String customerID = data[1];
                    System.out.println("customerID : " + customerID);

                    res = sr.getBookingSchedule(customerID);
                } else if (funct.length() >= 26 && funct.substring(0, 26).equals("listMovieShowsAvailability")) {
                    String movieName = data[1];
                    System.out.println("movieName : " + movieName);
                    res = sr.listMovieShow(movieName);
                } else if (funct.length() >= 18 && funct.substring(0, 18).equals("cancelMovieTickets")) {
                    String customerID = data[1];
                    String movieID = data[2];
                    String movieName = data[3];
                    String numOfTickets = data[4];
                    System.out.println("customerID: " + customerID);
                    System.out.println("movieID: " + movieID);
                    System.out.println("movieName: " + movieName);
                    System.out.println("numberOfTickets: " + numOfTickets);

                    int numberOfTickets = Integer.parseInt(numOfTickets);

                    res = sr.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
                } else if (funct.length() >= 17 && funct.substring(0, 17).equals("ExchangeTickets")) {
                    String customerID = data[1];
                    String movieID = data[2];
                    String old_movieName = data[3];
                    String new_movieID = data[4];
                    String new_movieName = data[5];
                    String numOfTickets = data[6];
                    System.out.println("customerID: " + customerID);
                    System.out.println("movieID: " + movieID);
                    System.out.println("old_movieName: " + old_movieName);
                    System.out.println("new_movieID: " + new_movieID);
                    System.out.println("new_movieName: " + new_movieName);
                    System.out.println("numberOfTickets: " + numOfTickets);

                    int numberOfTickets = Integer.parseInt(numOfTickets);

                    int x;
                    if (movieDatabase.get(new_movieName).containsKey(new_movieID)) {
                        x = movieDatabase.get(new_movieName).get(new_movieID).getBookingCapacity();
                    } else {
                        x = 0;
                    }
                    System.out.println(x);
                    if (x != 0 && x>numberOfTickets) {
                        res = "Available" + " " + new_movieName + " " + new_movieID;
                    } else {
                        res = "NotAvailable" + " " + new_movieName + " " + new_movieID;
                    }

                }


                System.out.println(res);
                byte[] b2 = (res + "").getBytes();
                InetAddress ia = InetAddress.getLocalHost();
                DatagramPacket dp1 = new DatagramPacket(b2, b2.length, ia, dp.getPort());
                ds.send(dp1);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}