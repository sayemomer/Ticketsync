package Booking;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebService(endpointInterface="Booking.BookingInterface")
@SOAPBinding(style=Style.RPC)


public class BookingImpl implements BookingInterface{

    String serverName;
    //made a constructor to initialize required a hashmap data
    BookingImpl(String serverName)  {
        this.serverName = serverName;
        dataInfo = new HashMap<>();
        dataInfo.put("Avatar", new HashMap<>());
        dataInfo.put("Titanic", new HashMap<>());
        dataInfo.put("Avenger", new HashMap<>());
    }

    public static HashMap<String, HashMap<String, MovieSlot>> dataInfo;
    //HashMap<String, Integer> Avatar;

    static final Logger logger = Logger.getLogger(BookingImpl.class.getName());
    Scanner sc = new Scanner(System.in);

    static {
        // Configure the logger to write to a text file
        try {
            FileHandler fileHandler = new FileHandler("mylog.txt");
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String addMovieSlots(String movieName, String movieID, int BookingCapacity) {

        LocalDate today = LocalDate.now();
        String dateString = movieID.substring(movieID.length() - 6);

        String formattedDate = String.format("%s-%s-%s", dateString.substring(0, 2), dateString.substring(2, 4), dateString.substring(4));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate dateToCheck = LocalDate.parse(formattedDate,formatter);


        if(!dateToCheck.isBefore(today) && !dateToCheck.isAfter(today.plusDays(6))) {


            //System.out.println("The date is within the next 7 days: " + withinNextWeek);

            String name = movieName;
            MovieSlot movieSlot = dataInfo.get(movieName).get(movieID);
            if (movieSlot == null) {
                movieSlot = new MovieSlot(movieID, BookingCapacity, name);
                dataInfo.get(movieName).put(movieID, movieSlot);
                System.out.println(movieSlot.getBookingCapacity());
                // Log the slot booking information
                logger.info(String.format("Movie slot added: movieName=%s, movieID=%s, bookingCapacity=%d", movieName, movieID, BookingCapacity));
                return "slot is booked successfully";

            } else {
                movieSlot.setBookingCapacity(BookingCapacity);

                // Log the slot capacity update information
                logger.info(String.format("Movie slot booking capacity updated: movieName=%s, movieID=%s, bookingCapacity=%d", movieName, movieID, BookingCapacity));
                return "slot booking capacity is updated successfully ";
            }
        }
        else{
            logger.info(String.format("Your date is not within same days"));
            return "date is not within the seven days";
        }
    }


    @Override
    public String removeMovieSlots(String movieID, String movieName)  {


        //        HashMap<String, MovieSlot> movieShow = dataInfo.get(movieName);
//        String nextSlotID = null;
//        if (movieShow.containsKey(movieID)) {
//
//            for (String SlotId : movieShow.keySet()) {
//                if (!SlotId.equals(movieID)) {
//                    nextSlotID = SlotId;
//                    break;
//                }
//            }
//            if (nextSlotID != null) {
//                dataInfo.get(movieName).remove(movieID);
//                logger.info(String.format("Movie slot %s removed from %s", movieID, movieName));
//                //dataInfo.get(movieName).get(movieID)
//            } else {
//                dataInfo.get(movieName).remove(movieID);
//                System.out.println("movInfo: " + dataInfo);
//                logger.info(String.format("Movie slot %s removed from %s. No more slots available.", movieID, movieName));
//                return "Slot is removed successfully";
//            }
//        } else {
//            logger.warning(String.format("Failed to remove movie slot %s from %s. Slot does not exist.", movieID, movieName));
//            return "there is no deletion can performed because Slot is not created";
//        }
//
//        return "Slot is removed successfully";
        String msg = null;
        if(dataInfo.get(movieName).containsKey(movieID)){
            if(dataInfo.get(movieName).get(movieID).getTickets() != null){
                System.out.println("ticket has found "+ dataInfo.get(movieName).get(movieID).getTickets());
                int sum = 0;
                for (int value : dataInfo.get(movieName).get(movieID).getTickets().values()) {
                    sum += value;
                }
                System.out.println("Sum of values in  Ticket HashMap: " + sum);
                String checkMovieID = movieID;//ATWM030323
                System.out.println("old movieID is " + checkMovieID);
                String subStr1 = checkMovieID.substring(0,3);
                String subStr2 = checkMovieID.substring(4,10);
                System.out.println("substr1  is " + subStr1);
                System.out.println("substr2  is " + subStr2);
                char ch;
                for(int i=0; i<21; i++){
                    ch  = checkMovieID.charAt(3);
                    if(ch == 'M'){
                        ch = 'A';
                    }else if(ch == 'A'){
                        ch = 'E';
                    }else if(ch == 'E') {
                        ch = 'M';
                        String dateString = subStr2;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
                        LocalDate date = LocalDate.parse(dateString, formatter);
                        LocalDate nextDate = date.plusDays(1);
                        subStr2 = nextDate.format(formatter);
                        System.out.println("date is " +subStr2);

                    }
                    checkMovieID = subStr1 + ch + subStr2;
                    System.out.println("for loop wali " +checkMovieID);
                    System.out.println(dataInfo.get(movieName).keySet());
                    Set<String> keySet = dataInfo.get(movieName).keySet();
                    String joinedKeys = String.join(", ", keySet);
                    System.out.println("keyset of datainfo "+joinedKeys);
                    if(joinedKeys.contains(checkMovieID)){
                        String newMovieID = checkMovieID;
                        System.out.println("newMovieID " + newMovieID);
                        int new_booking = 0;
                        for (int value : dataInfo.get(movieName).get(newMovieID).getTickets().values()) {
                            new_booking += value;
                        }
                        int nextSlotCapacity = dataInfo.get(movieName).get(newMovieID).getBookingCapacity();
                        nextSlotCapacity =  nextSlotCapacity - new_booking;
                        if(sum<=nextSlotCapacity){
                            MovieSlot Slot = dataInfo.get(movieName).get(movieID);
                            HashMap<String, Integer> oldTickets = Slot.getTickets();
                            System.out.println("old tickets: "+oldTickets);

                            MovieSlot new_Slot = dataInfo.get(movieName).get(newMovieID);
                            HashMap<String, Integer> newTickets = new_Slot.getTickets();
                            System.out.println("new tickets: "+newTickets);


                            HashMap<String, Integer> mergedMap = new HashMap<>();

                            for (Map.Entry<String, Integer> entry : newTickets.entrySet()) {
                                String key = entry.getKey();
                                int value = entry.getValue();
                                if (mergedMap.containsKey(key)) {
                                    value += mergedMap.get(key);
                                }
                                mergedMap.put(key, value);
                            }


                            for (Map.Entry<String, Integer> entry : oldTickets.entrySet()) {
                                String key = entry.getKey();
                                int value = entry.getValue();
                                if (mergedMap.containsKey(key)) {
                                    value += mergedMap.get(key);
                                }
                                mergedMap.put(key, value);
                            }

                            dataInfo.get(movieName).get(newMovieID).Tickets.putAll(mergedMap);
                            //System.out.println("after adding "+ mergedMap);
                            System.out.println("data info after adding old tickets into new tickets "+dataInfo.get(movieName).get(newMovieID).getTickets());
                            dataInfo.get(movieName).remove(movieID);
                            System.out.println("movieSlot is removed Successfully.");
                            msg = "movieSlot is removed Successfully.";
                            logger.info(String.format("Movie slot %s removed from %s. No more slots available.", newMovieID, movieName));
                            return msg;
                        }
                        break;
                    } else {
                        dataInfo.get(movieName).remove(movieID);
                        System.out.println("movieSlot is removed Successfully.");
                        msg = "movieSlot is removed Successfully.";

                    }
                }
            }
            else{
                dataInfo.get(movieName).remove(movieID);
                System.out.println("movieSlot is removed Successfully.");
                msg = "movieSlot is removed Successfully.";


            }
        }
        else{
            System.out.println("movie slot does not exist.");
            msg = "movie slot does not exist.";
            logger.warning("movieSlot does not exist.");
        }
        logger.info(String.format("Movie slot %s removed from %s. No more slots available.", movieID, movieName));
        return msg; //ATW M 020323
    }

    @Override
    public String listMovieShowsAvailability(String movieName) throws IOException {
        String x = String.valueOf(dataInfo.get(movieName));
        int index = x.indexOf("=");
        String movieID = "";
        //if(index>1) {
        movieID = x.substring(1, index);
        // }


        System.out.println(movieID);

        String a = "";


        HashMap<String, MovieSlot> slots = dataInfo.get(movieName);

        if (slots.containsKey(movieID)) {
            for (String slotId : slots.keySet()) {
                int BookCapacity = slots.get(slotId).getBookingCapacity() - slots.get(slotId).getBookTickets();
                System.out.println(slotId + ":" + movieName + ":" + BookCapacity);
                a = movieName +  ":" + slotId + ":" + BookCapacity;
                logger.info("Slot availability: " + slotId + ":" + movieName + ":" + BookCapacity);
            }


        } else {
            a = "slot is not created by admin";
            logger.warning("Slot not found: " + movieName);
        }

        DatagramSocket ds = new DatagramSocket();
        String sentence = "listMovieShowsAvailability" + " " + movieName;
        byte[] b1 = (sentence +"").getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println("Inet address"+ia);
        int port1 = 0;
        int port2 = 0;
        if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("ATW")){
            port1 = 6001;//verdun
            port2 = 6002;//outremont
        }
        else if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("VER")){
            port1 = 6000;//atwater
            port2 = 6002;//outremont
        }
        else if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("OUT")){
            port1 = 6000;//atwater
            port2 = 6001;//verdun
        }
        else{
            System.out.println("customer Id is not valid in udp client side. ");
        }
        System.out.println("Port 1"+" " + port1+" port 2 "+" " +port2+" " +"b1 "+b1);
        DatagramPacket dp1 = new DatagramPacket(b1, b1.length, ia, port1);
        DatagramPacket dp2 = new DatagramPacket(b1, b1.length, ia, port2);
        ds.send(dp1);
        ds.send(dp2);
        System.out.println("udp sent");


        String receive1;
        String receive2;
        byte[] b2 = new byte[1023];
        byte[] b3 = new byte[1023];
        DatagramPacket dp3 = new DatagramPacket(b2, b2.length);
        DatagramPacket dp4 = new DatagramPacket(b3, b3.length);
        ds.receive(dp3);
        ds.receive(dp4);


        receive1 = new String(dp3.getData(), 0, dp3.getLength());
        receive2 = new String(dp4.getData(), 0, dp4.getLength());
        System.out.println(receive1);
        System.out.println(receive2);
        logger.info("Received response from remote server: " + receive1);
        logger.info("Received response from remote server: " + receive2);

        return a + " Impl:  " + receive1 + " "+receive2;

    }

    int max_MoviesFrom_Other_Area =3;
    int count_Movies_From_other_area = 0;
    @Override
    public String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException {

        String a1 = movieID;
        String subStr1 = a1.substring(0, 3);
        String ab1 = "";
        String ab2 = "";


        if (subStr1.equals("ATW")) {
            ab1 = "VER" + a1.substring(3, 10);
            ab2 = "OUT" + a1.substring(3, 10);
        } else if (subStr1.equals("VER")) {
            ab1 = "ATW" + a1.substring(3, 10);
            ab2 = "OUT" + a1.substring(3, 10);
        } else if (subStr1.equals("OUT")) {
            ab1 = "VER" + a1.substring(3, 10);
            ab2 = "ATW" + a1.substring(3, 10);
        }
        System.out.println("ab1: " + ab1);
        System.out.println("ab2: " + ab2);
        if (dataInfo.get(movieName).containsKey(ab1)  || dataInfo.get(movieName).containsKey(ab2)) {
            if(dataInfo.get(movieName).get(ab1).Tickets.containsKey(customerID) ||  ( dataInfo.get(movieName).containsKey(ab2) && dataInfo.get(movieName).get(ab2).Tickets.containsKey(customerID)) ) {
                System.out.println("you cannot book same tickets in different server");
                logger.info("you cannot book same tickets in different server");
                return "you cannot book same tickets in different server";
            }else {

                String receive = "";
                if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
                    HashMap<String, MovieSlot> movieShow = dataInfo.get(movieName);
                    if (movieShow == null) {
                        logger.warning("Movie not found: " + movieName);
                        return "false : it cannot be booked";
                    }
                    if (movieShow.containsKey(movieID)) {
                        if (movieShow.get(movieID).getBookingCapacity() - movieShow.get(movieID).getBookTickets() >= numberOfTickets) {
                            // String ticketID = generateUniqueID();
                            movieShow.get(movieID).getTickets().put(customerID, numberOfTickets);
                            String res = null;
                            for (String val : movieShow.get(movieID).Tickets.keySet()) {

                                res = movieShow.get(movieID).Tickets.get(customerID) + " tickets booked successfully for " + movieName;
                            }
                            System.out.println(res);
                            logger.info("Tickets booked for customer " + customerID + ": " + movieShow.get(movieID).Tickets.get(customerID) + " tickets booked successfully for " + movieName);
                            return "true : ticket has booked successfully.";

                        } else {
                            logger.warning("Not enough tickets available for movie " + movieName);
                            System.out.println("Tickets are not available.");
                            return "false : Tickets are not available.";
                        }
                    } else {
                        logger.warning("Movie slot not found for movie " + movieName + " and slot ID " + movieID);
                        System.out.println("This " + movieName + " slot is not created");
                        return "false : slot is not available for book tickets.";
                    }
                } else {
                    //UDP client

                    if (count_Movies_From_other_area < max_MoviesFrom_Other_Area) {
                        DatagramSocket ds = new DatagramSocket();

                        String sentence = "bookMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
                        byte[] b1 = (sentence + "").getBytes();
                        InetAddress ia = InetAddress.getLocalHost();
                        int port = 0;
                        if (movieID.substring(0, 3).equals("VER")) {
                            port = 6001;
                        } else if (movieID.substring(0, 3).equals("ATW")) {
                            port = 6000;
                        } else if (movieID.substring(0, 3).equals("OUT")) {
                            port = 6002;
                        } else {
                            System.out.println("movie id is not valid in udp client");
                        }
                        DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
                        ds.send(dp);


                        byte[] b2 = new byte[1024];
                        DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                        ds.receive(dp1);

                        receive = new String(dp1.getData(), 0, dp1.getLength());
                        logger.info("Received response from remote server: " + receive);
                        if(receive.substring(0,4).equals("true")) {
                            count_Movies_From_other_area++;
                        }

                    } else {
                        receive = "you have reached the limit that you can only book 3 movies at most from other areas overall in a week. ";
                        logger.warning(receive);
                    }

                    return receive;
                }

            }
        }
        else {

            String receive = "";
            System.out.println("in else part of book movie tickets");
            if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
                HashMap<String, MovieSlot> movieShow = dataInfo.get(movieName);
                if (movieShow == null) {
                    logger.warning("Movie not found: " + movieName);
                    return "false : it cannot be booked";
                }
                if (movieShow.containsKey(movieID)) {
                    if (movieShow.get(movieID).getBookingCapacity() - movieShow.get(movieID).getBookTickets() >= numberOfTickets) {
                        // String ticketID = generateUniqueID();
                        movieShow.get(movieID).getTickets().put(customerID, numberOfTickets);
                        String res = null;
                        for (String val : movieShow.get(movieID).Tickets.keySet()) {

                            res = movieShow.get(movieID).Tickets.get(customerID) + " tickets booked successfully for " + movieName;
                        }
                        System.out.println(res);
                        logger.info("Tickets booked for customer " + customerID + ": " + movieShow.get(movieID).Tickets.get(customerID) + " tickets booked successfully for " + movieName);
                        return "true : ticket has booked successfully.";

                    } else {
                        logger.warning("Not enough tickets available for movie " + movieName);
                        System.out.println("Tickets are not available.");
                        return "false : Tickets are not available.";
                    }
                } else {
                    logger.warning("Movie slot not found for movie " + movieName + " and slot ID " + movieID);
                    System.out.println("This " + movieName + " slot is not created");
                    return "false : slot is not available for book tickets.";
                }
            } else {
                //UDP client

                if (count_Movies_From_other_area < max_MoviesFrom_Other_Area) {
                    DatagramSocket ds = new DatagramSocket();

                    String sentence = "bookMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
                    byte[] b1 = (sentence + "").getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    int port = 0;
                    if (movieID.substring(0, 3).equals("VER")) {
                        port = 6001;
                    } else if (movieID.substring(0, 3).equals("ATW")) {
                        port = 6000;
                    } else if (movieID.substring(0, 3).equals("OUT")) {
                        port = 6002;
                    } else {
                        System.out.println("movie id is not valid in udp client");
                    }
                    DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
                    ds.send(dp);


                    byte[] b2 = new byte[1024];
                    DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                    ds.receive(dp1);

                    receive = new String(dp1.getData(), 0, dp1.getLength());
                    logger.info("Received response from remote server: " + receive);
                    if(receive.substring(0,4).equals("true")) {
                        count_Movies_From_other_area++;
                    }

                } else {
                    receive = "you have reached the limit that you can only book 3 movies at most from other areas overall in a week. ";
                    logger.warning(receive);
                }

                return receive;
            }

        }

    }


    @Override
    public String getBookingSchedule(String customerID) throws IOException {
        String s = "";
        for(String movieName : dataInfo.keySet()){
            HashMap<String, MovieSlot> slots = dataInfo.get(movieName);

            for(String slotId: slots.keySet()){
                int numberOfTickets = slots.get(slotId).getTickets().getOrDefault(customerID,0);

                System.out.println(slotId + ":" + movieName + ":" + String.valueOf(numberOfTickets));
                logger.info(slotId + ":" + movieName + ":" + String.valueOf(numberOfTickets));
                s = movieName +":" + slotId +  ":" + String.valueOf(numberOfTickets);
            }
        }
        DatagramSocket ds = new DatagramSocket();
        String sentence = "getBookingSchedule" + " " + customerID;
        byte[] b1 = (sentence +"").getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println("Inet address"+ia);
        int port1 = 0;
        int port2 = 0;
        if(customerID.substring(0,3).equals("ATW")){
            port1 = 6001;//verdun
            port2 = 6002;//outremont
        }
        else if(customerID.substring(0,3).equals("VER")){
            port1 = 6000;//atwater
            port2 = 6002;//outremont
        }
        else if(customerID.substring(0,3).equals("OUT")){
            port1 = 6000;//atwater
            port2 = 6001;//verdun
        }
        else{
            System.out.println("customer Id is not valid in udp client side. ");
        }
        System.out.println("Port 1"+" " + port1+"port 2"+" " +port2+" " +"b1"+b1);
        DatagramPacket dp1 = new DatagramPacket(b1, b1.length, ia, port1);
        DatagramPacket dp2 = new DatagramPacket(b1, b1.length, ia, port2);
        ds.send(dp1);
        ds.send(dp2);
        System.out.println("udp sent");

        String receive1;
        String receive2;
        byte[] b2 = new byte[1023];
        byte[] b3 = new byte[1023];
        DatagramPacket dp3 = new DatagramPacket(b2, b2.length);
        DatagramPacket dp4 = new DatagramPacket(b3, b3.length);
        ds.receive(dp3);
        ds.receive(dp4);


        receive1 = new String(dp3.getData(), 0, dp3.getLength());
        receive2 = new String(dp4.getData(), 0, dp4.getLength());
        System.out.println(receive1);
        System.out.println(receive2);
        logger.info("Received response from remote server: " + receive1);
        logger.info("Received response from remote server: " + receive2);

        return s + " Impl:  " + receive1 + " "+receive2;
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException {
        String receive = "";
        if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
            if (dataInfo.get(movieName).get(movieID).Tickets.containsKey(customerID)) {
                System.out.println("total book tickets of customer: ");
                System.out.println(dataInfo.get(movieName).get(movieID).getTickets().get(customerID));

                logger.log(Level.INFO, "Total book tickets of customer: " + dataInfo.get(movieName).get(movieID).getTickets().get(customerID));
                int num = dataInfo.get(movieName).get(movieID).Tickets.get(customerID) - numberOfTickets;
                dataInfo.get(movieName).get(movieID).setBookTickets(num);

                System.out.println("Remaining Tickets: ");
                System.out.println(dataInfo.get(movieName).get(movieID).getBookTickets());
                logger.log(Level.INFO, "Remaining Tickets: " + dataInfo.get(movieName).get(movieID).getBookTickets());
                return "Ticket is cancelled successfully";
            } else {
                return "this customerId is not available in our database";
            }
        } else {
            //UDP client
            DatagramSocket ds = new DatagramSocket();

            String sentence = "cancelMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
            byte[] b1 = (sentence + "").getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            int port = 0;
            if (movieID.substring(0, 3).equals("VER")) {
                port = 6001;
            } else if (movieID.substring(0, 3).equals("ATW")) {
                port = 6000;
            } else if (movieID.substring(0, 3).equals("OUT")) {
                port = 6002;
            } else {
                System.out.println("movie id is not valid in udp client");
            }
            DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
            ds.send(dp);


            byte[] b2 = new byte[1024];
            DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
            ds.receive(dp1);

            receive = new String(dp1.getData(), 0, dp1.getLength());
            logger.info("Received response from remote server: " + receive);
            return receive;

        }
    }

    @Override
    public String exchangeTickets(String customerID,String movieID, String new_movieID,String new_movieName, int numberOfTickets) throws IOException {
        String targetValue = movieID;
        String targetKey = null;

        for (Map.Entry<String,  HashMap<String, MovieSlot>> entry : dataInfo.entrySet()) {
            System.out.println("loop is runnning");
            System.out.println(entry.getValue());
            System.out.println(targetValue);
            String x = String.valueOf(entry.getValue());
            System.out.println(x);
            if (x.contains(targetValue)) {
                targetKey = entry.getKey();
                System.out.println("if condition enter");
                System.out.println(targetKey);
                break;
            }
        }
        System.out.println("old movieName is " +targetKey);

        String FinalMsg = null;
        if(dataInfo.get(targetKey).get(movieID).getSlotID().equals(movieID)){
            if(dataInfo.get(targetKey).get(movieID).getTickets().containsKey(customerID)) {
                System.out.println(customerID + " has already booked Tickets for this " + movieID);
                DatagramSocket ds = new DatagramSocket();
                String old_movieName = targetKey;
                String sentence = "CheckAvailability" +" " + customerID + " " + movieID + " "+ old_movieName +" " + new_movieID + " " + new_movieName + " " + numberOfTickets;
                byte[] b1 = (sentence+"").getBytes();
                InetAddress ia = InetAddress.getLocalHost();
                int port = 0;
                if (new_movieID.substring(0, 3).equals("VER")) {
                    port = 6001;
                } else if (new_movieID.substring(0, 3).equals("ATW")) {
                    port = 6000;
                } else if (new_movieID.substring(0, 3).equals("OUT")) {
                    port = 6002;
                } else {
                    System.out.println("movie id is not valid in udp client");
                }
                DatagramPacket dp = new DatagramPacket(b1,b1.length,ia,port);
                ds.send(dp);

                byte[] b2 = new byte[1024];
                DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                ds.receive(dp1);
                String msg = new String(dp1.getData(), 0, dp1.getLength());
                System.out.println("Received message is : " + msg);
                String[] data = msg.split(" ");
                String check = data[0];
                System.out.println(check);

                if(check.equals("Available")){
                    String a = bookMovieTickets(customerID,new_movieID,new_movieName,numberOfTickets);
                    System.out.println(a);
                    int y = dataInfo.get(old_movieName).get(movieID).getTickets().get(customerID);
                    String b = cancelMovieTickets(customerID,movieID,old_movieName,y);
                    System.out.println(b);
                    FinalMsg =  "exchange of tickets are done successfully";
                    logger.info(FinalMsg);
                }else {
                    FinalMsg = "cannot perform exchange of tickets operation.";
                    logger.info(FinalMsg);
                }
            }
            else{
                System.out.println(customerID + " did not book the tickets for this "+ movieID);
                FinalMsg =  customerID + " did not book the tickets for this "+ movieID;
                logger.warning(FinalMsg);
            }
        }
        return FinalMsg;
    }

}
