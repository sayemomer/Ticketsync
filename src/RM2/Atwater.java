package RM2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static RM2.BookingImpl.dataInfo;

public class Atwater {
    public static void main(String[] args) throws IOException {

        BookingImpl obj = new BookingImpl("ATWATER");
        DatagramSocket asocket = new DatagramSocket(8889);
        byte[] buffer = new byte[1024];
        DatagramPacket apacket = new DatagramPacket(buffer, buffer.length);
        System.out.println("Atwater server is  ready and waiting ...");


        BookingImpl sr = new BookingImpl("ATWATER");
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
            System.out.println("Received sentence from RM2 is : " + sentence);
            String[] Info = sentence.split(";");
            String method_to_invoke_from_BookingImpl = Info[3];
            String response = null;
            if(method_to_invoke_from_BookingImpl.equalsIgnoreCase("addMovieSlots")){
                String movieName = Info[6];
                String movieID = Info[5];
                String bookCapacity = Info[9];

                System.out.println("movieName: " + movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("bookingCapacity e: " + bookCapacity);

                int BookingCapacity = Integer.parseInt(bookCapacity);
                response = obj.addMovieSlots(movieName, movieID, BookingCapacity);
            } else if(method_to_invoke_from_BookingImpl.equalsIgnoreCase("removeMovieSlots")){
                String movieId = Info[5];
                String movieName = Info[6];
                response = obj.removeMovieSlots(movieId, movieName);
            } else if(method_to_invoke_from_BookingImpl.equalsIgnoreCase("listMovieShowsAvailability")){
                String movieName = Info[6];
                response = obj.listMovieShowsAvailability(movieName);
            } else if (method_to_invoke_from_BookingImpl.equalsIgnoreCase("bookMovieTickets")){
                String customerID = Info[4];
                String movieName = Info[6];
                String movieID = Info[5];
                String tickets = Info[9];

                System.out.println("customerID: "+customerID);
                System.out.println("movieName: " + movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("number of tickets: " + tickets);

                int number_of_tickets = Integer.parseInt(tickets);
                response = obj.bookMovieTickets(customerID,movieID, movieName, number_of_tickets);
            } else if(method_to_invoke_from_BookingImpl.equalsIgnoreCase("getBookingSchedule")){
                String customerID = Info[4];
                response = obj.getBookingSchedule(customerID);
            } else if (method_to_invoke_from_BookingImpl.equalsIgnoreCase("cancelMovieTickets")){
                String customerID = Info[4];
                String movieName = Info[6];
                String movieID = Info[5];
                String tickets = Info[9];

                System.out.println("customerID: "+customerID);
                System.out.println("movieName: " + movieName);
                System.out.println("movieID: " + movieID);
                System.out.println("number of tickets: " + tickets);

                int number_of_tickets = Integer.parseInt(tickets);
                response = obj.cancelMovieTickets(customerID,movieID, movieName, number_of_tickets);
            } else if (method_to_invoke_from_BookingImpl.equalsIgnoreCase("exchangeTickets")){
                String customerID = Info[4];
                String movieID = Info[7];
                String new_movieName = Info[6];
                String new_movieID = Info[5];
                String tickets = Info[9];

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
            //Endpoint endpoint = Endpoint.publish("http://localhost:8086/Booking", obj);
            //System.out.println("Booking service is published: " + endpoint.isPublished());


        }
    }

    public static void UDPServer(BookingImpl sr) throws IOException {
        try {

            DatagramSocket ds = new DatagramSocket(6000);

            byte[] b1 = new byte[1024];
            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            System.out.println("UDP server is started...");
            while (true) {
                ds.receive(dp);
                String sentence = new String(dp.getData(), 0, dp.getLength());
                System.out.println("Received sentence is : " + sentence);
                String[] data = sentence.split(" ");
                String funct = data[0];
                functions fun = new functions();
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
                    if (dataInfo.get(movieName).get(movieID).Tickets.containsKey(customerID)) {
                        res = "It cannot book the tickets of the same movie for the same show in different theatres that you have already booked.";
                    } else {
                        res = sr.bookMovieTickets(customerID, movieID, movieName, numberOfTickets);
                    }
                } else if (funct.length() >= 18 && funct.substring(0, 18).equals("getBookingSchedule")) {
                    String customerID = data[1];
                    System.out.println("customerID : " + customerID);

                    res = fun.getBookingSchedule(customerID);
                } else if (funct.length() >= 26 && funct.substring(0, 26).equals("listMovieShowsAvailability")) {
                    String movieName = data[1];
                    System.out.println("movieName : " + movieName);
                    res = fun.listMovieShow(movieName);
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
                } else if (funct.length() >= 17 && funct.substring(0, 17).equals("CheckAvailability")) {
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
                    if (dataInfo.get(new_movieName).containsKey(new_movieID)) {
                        x = dataInfo.get(new_movieName).get(new_movieID).getBookingCapacity();
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