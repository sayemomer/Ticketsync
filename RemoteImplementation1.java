package Assignment3;


import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;




public class RemoteImplementation1 implements RemoteInterface1{

    String serverName;

    RemoteImplementation1(String serverName)  {
        this.serverName = serverName;
        movieDatabase = new HashMap<>();
        movieDatabase.put("Avatar", new HashMap<>());
        movieDatabase.put("Titanic", new HashMap<>());
        movieDatabase.put("Avenger", new HashMap<>());
    }

    public static HashMap<String, HashMap<String, info>> movieDatabase;
    //HashMap<String, Integer> Avatar;

    Scanner sc = new Scanner(System.in);


    public String addMovieSlots(String movieName, String movieID, int BookingCapacity) {

        String date = movieID.substring(4);
        SimpleDateFormat sdf = new SimpleDateFormat("yymmdd");
        Date startDate = null;
        try {
            startDate = sdf.parse("230314");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ;
        Date endDate = null;
        try {
            endDate = sdf.parse("230320");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ;

        dateValidate dv = new dateValidate(startDate, endDate);

        Date newdate = null;
        try {
            newdate = new SimpleDateFormat("yymmdd").parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (dv.isWithinRange(newdate)) {


            //System.out.println("The date is within the next 7 days: " + withinNextWeek);

            String name = movieName;
            info info = movieDatabase.get(movieName).get(movieID);
            if (info == null) {
                info = new info(movieID, BookingCapacity, name);
                movieDatabase.get(movieName).put(movieID, info);

                // Log the slot booking information

                return "Movie Slot Created";

            } else {
                info.setBookingCapacity(BookingCapacity);

                // Log the slot capacity update information
                return "Updated ";
            }
        }
        else{
            return "Date is not In the Range";
        }
    }



    public String removeMovieSlots(String movieID, String movieName)  {

        String message = null;
        if(movieDatabase.get(movieName).containsKey(movieID)){
            if(movieDatabase.get(movieName).get(movieID).getTickets() != null){
                System.out.println( movieDatabase.get(movieName).get(movieID).getTickets());
                int sum = 0;
                for (int value : movieDatabase.get(movieName).get(movieID).getTickets().values()) {
                    sum += value;
                }
                System.out.println( sum);
                String checkMovieID = movieID;
                System.out.println( checkMovieID);
                String subStr1 = checkMovieID.substring(0,3);
                String subStr2 = checkMovieID.substring(4,10);

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
                    System.out.println(checkMovieID);
                    System.out.println(movieDatabase.get(movieName).keySet());
                    Set<String> valuePair = movieDatabase.get(movieName).keySet();
                    String pairs = String.join(", ", valuePair);
                    
                    if(pairs.contains(checkMovieID)){
                        String newMovieID = checkMovieID;
                        System.out.println( newMovieID);
                        int new_booking = 0;
                        for (int value : movieDatabase.get(movieName).get(newMovieID).getTickets().values()) {
                            new_booking += value;
                        }
                        int nextSlotCapacity = movieDatabase.get(movieName).get(newMovieID).getBookingCapacity();
                        nextSlotCapacity =  nextSlotCapacity - new_booking;
                        if(sum<=nextSlotCapacity){
                            info Slot = movieDatabase.get(movieName).get(movieID);
                            HashMap<String, Integer> oldTickets = Slot.getTickets();
                            System.out.println("old tickets: "+oldTickets);

                            info new_Slot = movieDatabase.get(movieName).get(newMovieID);
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

                            movieDatabase.get(movieName).get(newMovieID).Tickets.putAll(mergedMap);

                            movieDatabase.get(movieName).remove(movieID);
                            System.out.println("info is removed Successfully.");
                            message = "info is removed Successfully.";

                            return message;
                        }
                        break;
                    } else {
                        movieDatabase.get(movieName).remove(movieID);

                        message = "info is removed Successfully.";

                    }
                }
            }
            else{
                movieDatabase.get(movieName).remove(movieID);
                System.out.println("info is removed Successfully.");
                message = "info is removed Successfully.";


            }
        }
        else{
            System.out.println("movie slot does not exist.");
            message = "movie slot does not exist.";

        }

        return message; 
    }

    @Override
    public String listMovieShowsAvailability(String movieName) throws IOException {
        String x = String.valueOf(movieDatabase.get(movieName));
        int index = x.indexOf("=");
        String movieID = "";
        //if(index>1) {
        movieID = x.substring(1, index);
        // }


        System.out.println(movieID);

        String a = "";


        HashMap<String, info> slots = movieDatabase.get(movieName);

        if (slots.containsKey(movieID)) {
            for (String slots1 : slots.keySet()) {
                int BookCapacity = slots.get(slots1).getBookingCapacity() - slots.get(slots1).getBookTickets();
                System.out.println(slots + ":" + movieName + ":" + BookCapacity);
                a = movieName +  ":" + slots + ":" + BookCapacity;

            }


        } else {
            a = "slot is not created by admin";

        }

        DatagramSocket ds = new DatagramSocket();
        String sentence = "listMovieShowsAvailability" + " " + movieName;
        byte[] b1 = (sentence +"").getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println("Inet address"+ia);
        int port1 = 0;
        int port2 = 0;
        if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("ATW")){
            port1 = 6123;//verdun
            port2 = 6124;//outremont
        }
        else if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("VER")){
            port1 = 6122;//atwater
            port2 = 6124;//outremont
        }
        else if(movieID != null && !movieID.isEmpty() && movieID.substring(0,3).equals("OUT")){
            port1 = 6122;//atwater
            port2 = 6123;//verdun
        }
        else{
            System.out.println("Invalid CustomerID");
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


        return a + " Impl:  " + receive1 + " "+receive2;

    }

    int max_MoviesFrom_Other_Area =3;
    int ticketsInOtherServer = 0;
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

        if (movieDatabase.get(movieName).containsKey(ab1)  || movieDatabase.get(movieName).containsKey(ab2)) {
            if(movieDatabase.get(movieName).get(ab1).Tickets.containsKey(customerID) ||  ( movieDatabase.get(movieName).containsKey(ab2) && movieDatabase.get(movieName).get(ab2).Tickets.containsKey(customerID)) ) {
                System.out.println("Can't Book");

                return "Can't Book";
            }else {

                String receive = "";
                if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
                    HashMap<String, info> movieShow = movieDatabase.get(movieName);
                    if (movieShow == null) {

                        return "false : it cannot be booked";
                    }
                    if (movieShow.containsKey(movieID)) {
                        if (movieShow.get(movieID).getBookingCapacity() - movieShow.get(movieID).getBookTickets() >= numberOfTickets) {
                            // String ticketID = generateUniqueID();
                            movieShow.get(movieID).getTickets().put(customerID, numberOfTickets);
                            String res = null;
                            for (String val : movieShow.get(movieID).Tickets.keySet()) {

                                res = movieShow.get(movieID).Tickets.get(customerID) + " Movie Booked Successfully " + movieName;
                            }
                            System.out.println(res);

                            return "true : ticket has booked successfully.";

                        } else {

                            System.out.println("Tickets are not available.");
                            return "false : Tickets are not available.";
                        }
                    } else {

                        System.out.println("This " + movieName + " slot is not created");
                        return "false : slot is not available for book tickets.";
                    }
                } else {
                    

                    if (ticketsInOtherServer < max_MoviesFrom_Other_Area) {
                        DatagramSocket ds = new DatagramSocket();

                        String sentence = "bookMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
                        byte[] b1 = (sentence + "").getBytes();
                        InetAddress ia = InetAddress.getLocalHost();
                        int port = 0;
                        if (movieID.substring(0, 3).equals("VER")) {
                            port = 6123;
                        } else if (movieID.substring(0, 3).equals("ATW")) {
                            port = 6122;
                        } else if (movieID.substring(0, 3).equals("OUT")) {
                            port = 6124;
                        } else {
                            System.out.println("Invalid Movie ID");
                        }
                        DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
                        ds.send(dp);


                        byte[] b2 = new byte[1024];
                        DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                        ds.receive(dp1);

                        receive = new String(dp1.getData(), 0, dp1.getLength());

                        if(receive.substring(0,4).equals("true")) {
                            ticketsInOtherServer++;
                        }

                    } else {
                        receive = "Can't Book ";

                    }

                    return receive;
                }

            }
        }
        else {

            String receive = "";
            if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
                HashMap<String, info> movieShow = movieDatabase.get(movieName);
                if (movieShow == null) {

                    return "false : it cannot be booked";
                }
                if (movieShow.containsKey(movieID)) {
                    if (movieShow.get(movieID).getBookingCapacity() - movieShow.get(movieID).getBookTickets() >= numberOfTickets) {
                        // String ticketID = generateUniqueID();
                        movieShow.get(movieID).getTickets().put(customerID, numberOfTickets);
                        String res = null;
                        for (String val : movieShow.get(movieID).Tickets.keySet()) {

                            res = movieShow.get(movieID).Tickets.get(customerID) + " Movie Booked Succesfully " + movieName;
                        }
                        System.out.println(res);

                        return "true : ticket has booked successfully.";

                    } else {

                        System.out.println("Tickets are not available.");
                        return "false : Tickets are not available.";
                    }
                } else {

                    System.out.println("This " + movieName + " slot is not created");
                    return "false : slot is not available for book tickets.";
                }
            } else {
                

                if (ticketsInOtherServer < max_MoviesFrom_Other_Area) {
                    DatagramSocket ds = new DatagramSocket();

                    String sentence = "bookMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
                    byte[] b1 = (sentence + "").getBytes();
                    InetAddress ia = InetAddress.getLocalHost();
                    int port = 0;
                    if (movieID.substring(0, 3).equals("VER")) {
                        port = 6123;
                    } else if (movieID.substring(0, 3).equals("ATW")) {
                        port = 6122;
                    } else if (movieID.substring(0, 3).equals("OUT")) {
                        port = 6124;
                    } else {
                        System.out.println("Invalid Movie ID");
                    }
                    DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
                    ds.send(dp);


                    byte[] b2 = new byte[1024];
                    DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                    ds.receive(dp1);

                    receive = new String(dp1.getData(), 0, dp1.getLength());

                    if(receive.substring(0,4).equals("true")) {
                        ticketsInOtherServer++;
                    }

                } else {
                    receive = "you have reached the limit that you can only book 3 movies at most from other areas overall in a week. ";

                }

                return receive;
            }

        }

    }
    public String getBookingSchedules(String customerID) {
        String s = "";
        for(String movieName : movieDatabase.keySet()){
            HashMap<String, info> slots = movieDatabase.get(movieName);

            for(String slots1: slots.keySet()){
                int numberOfTickets = slots.get(slots1).getTickets().getOrDefault(customerID,0);

                System.out.println(slots + ":" + movieName + ":" + String.valueOf(numberOfTickets));

                s = movieName +":" + slots +  ":" + String.valueOf(numberOfTickets);
            }
        }
        return s;
    }

    public String listMovieShow(String movieName)  {
        String x = String.valueOf(movieDatabase.get(movieName));
        int index = x.indexOf("=");
        String movieID = "";
        if(index>1) {
            movieID = x.substring(1, index);
        }
        System.out.println(movieID);

        String a = "";


        HashMap<String, info> slots = movieDatabase.get(movieName);


        if (slots.containsKey(movieID)) {
            for (String slots1 : slots.keySet()) {
                int numberOfTickets = slots.get(slots1).getBookingCapacity() - slots.get(slots1).getBookTickets();
                System.out.println(slots + ":" + movieName + ":" + numberOfTickets);
                a = movieName + ":" + slots + ":" + numberOfTickets;


            }


        }
        return a;
    }


    @Override
    public String getBookingSchedule(String customerID) throws IOException {
        String s = "";
        for(String movieName : movieDatabase.keySet()){
            HashMap<String, info> slots = movieDatabase.get(movieName);

            for(String slots1: slots.keySet()){
                int numberOfTickets = slots.get(slots1).getTickets().getOrDefault(customerID,0);

                System.out.println(slots + ":" + movieName + ":" + String.valueOf(numberOfTickets));

                s = movieName +":" + slots +  ":" + String.valueOf(numberOfTickets);
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
            port1 = 6123;//verdun
            port2 = 6124;//outremont
        }
        else if(customerID.substring(0,3).equals("VER")){
            port1 = 6122;//atwater
            port2 = 6124;//outremont
        }
        else if(customerID.substring(0,3).equals("OUT")){
            port1 = 6122;//atwater
            port2 = 6123;//verdun
        }
        else{
            System.out.println("Invalid Customer Id");
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

        return s + " Impl:  " + receive1 + " "+receive2;
    }

    @Override
    public String cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException {
        String receive = "";
        if (movieID.substring(0, 3).equals(serverName.substring(0, 3))) {
            if (movieDatabase.get(movieName).get(movieID).Tickets.containsKey(customerID)) {
                System.out.println("total book tickets of customer: ");
                System.out.println(movieDatabase.get(movieName).get(movieID).getTickets().get(customerID));


                int num = movieDatabase.get(movieName).get(movieID).Tickets.get(customerID) - numberOfTickets;
                movieDatabase.get(movieName).get(movieID).setBookTickets(num);

                System.out.println("Remaining Tickets: ");
                System.out.println(movieDatabase.get(movieName).get(movieID).getBookTickets());

                return "Ticket is cancelled successfully";
            } else {
                return "this customerId is not available in our database";
            }
        } else {
            
            DatagramSocket ds = new DatagramSocket();

            String sentence = "cancelMovieTickets " + customerID + " " + movieID + " " + movieName + " " + numberOfTickets;
            byte[] b1 = (sentence + "").getBytes();
            InetAddress ia = InetAddress.getLocalHost();
            int port = 0;
            if (movieID.substring(0, 3).equals("VER")) {
                port = 6123;
            } else if (movieID.substring(0, 3).equals("ATW")) {
                port = 6122;
            } else if (movieID.substring(0, 3).equals("OUT")) {
                port = 6124;
            } else {
                System.out.println("Invalid Movie ID");
            }
            DatagramPacket dp = new DatagramPacket(b1, b1.length, ia, port);
            ds.send(dp);


            byte[] b2 = new byte[1024];
            DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
            ds.receive(dp1);

            receive = new String(dp1.getData(), 0, dp1.getLength());

            return receive;

        }
    }

    @Override
    public String exchangeTickets(String customerID,String movieID, String new_movieID,String new_movieName, int numberOfTickets) throws IOException {
        String targetValue = movieID;
        String targetKey = null;

        for (Map.Entry<String,  HashMap<String, info>> entry : movieDatabase.entrySet()) {

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

        String Message = null;
        if(movieDatabase.get(targetKey).get(movieID).getSlotID().equals(movieID)){
            if(movieDatabase.get(targetKey).get(movieID).getTickets().containsKey(customerID)) {
                System.out.println(customerID + " has already booked Tickets for this " + movieID);
                DatagramSocket ds = new DatagramSocket();
                String old_movieName = targetKey;
                String sentence = "CheckAvailability" +" " + customerID + " " + movieID + " "+ old_movieName +" " + new_movieID + " " + new_movieName + " " + numberOfTickets;
                byte[] b1 = (sentence+"").getBytes();
                InetAddress ia = InetAddress.getLocalHost();
                int port = 0;
                if (new_movieID.substring(0, 3).equals("VER")) {
                    port = 6123;
                } else if (new_movieID.substring(0, 3).equals("ATW")) {
                    port = 6122;
                } else if (new_movieID.substring(0, 3).equals("OUT")) {
                    port = 6124;
                } else {
                    System.out.println("No Booking Found");
                }
                DatagramPacket dp = new DatagramPacket(b1,b1.length,ia,port);
                ds.send(dp);

                byte[] b2 = new byte[1024];
                DatagramPacket dp1 = new DatagramPacket(b2, b2.length);
                ds.receive(dp1);
                String message = new String(dp1.getData(), 0, dp1.getLength());
                System.out.println("Received message is : " + message);
                String[] data = message.split(" ");
                String check = data[0];
                System.out.println(check);

                if(check.equals("Available")){
                    String a = bookMovieTickets(customerID,new_movieID,new_movieName,numberOfTickets);
                    System.out.println(a);
                    int y = movieDatabase.get(old_movieName).get(movieID).getTickets().get(customerID);
                    String b = cancelMovieTickets(customerID,movieID,old_movieName,y);
                    System.out.println(b);
                    Message =  "Exchanged";

                }else {
                    Message = "Exchanged Failed";

                }
            }
            else{
                System.out.println(customerID + " No Tickets "+ movieID);

            }
        }
        return Message;
    }

}
