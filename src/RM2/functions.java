package RM2;

import java.util.HashMap;
import java.util.Scanner;

import static RM2.BookingImpl.dataInfo;
import static RM2.BookingImpl.logger;


public class functions {
    Scanner sc = new Scanner(System.in);
    public String getBookingSchedule(String customerID) {
        String s = "";
        for(String movieName : dataInfo.keySet()){
            HashMap<String, MovieSlot> slots = dataInfo.get(movieName);

            for(String slotId: slots.keySet()){
                int numberOfTickets = slots.get(slotId).getTickets().getOrDefault(customerID,0);

                System.out.println(slotId + ":" + movieName + ":" + String.valueOf(numberOfTickets));

                s = movieName +":" + slotId +  ":" + String.valueOf(numberOfTickets);
            }
        }
        return s;
    }

    public String listMovieShow(String movieName)  {
        String x = String.valueOf(dataInfo.get(movieName));
        int index = x.indexOf("=");
        String movieID = "";
        if(index>1) {
            movieID = x.substring(1, index);
        }
        System.out.println(movieID);

        String a = "";


        HashMap<String, MovieSlot> slots = dataInfo.get(movieName);


        if (slots.containsKey(movieID)) {
            for (String slotId : slots.keySet()) {
                int numberOfTickets = slots.get(slotId).getBookingCapacity() - slots.get(slotId).getBookTickets();
                System.out.println(slotId + ":" + movieName + ":" + numberOfTickets);
                a = movieName + ":" + slotId + ":" + numberOfTickets;
                logger.info("Slot availability: " + slotId + ":" + movieName + ":" + numberOfTickets);

            }


        } else {
            logger.warning("Slot not found: " + movieName);
            a = "slot is not created by admin";

        }
        return a;
    }
}