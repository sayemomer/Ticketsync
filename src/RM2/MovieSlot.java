package RM2;

import java.util.HashMap;

public class MovieSlot {
    String slotID;
    int bookingCapacity;
    int bookTickets;
    String movieName;
    HashMap<String, Integer> Tickets = new HashMap<>();

    public MovieSlot(String slotID, int bookingCapacity, String movieName) {
        this.slotID = slotID;
        bookTickets = 0;
        this.bookingCapacity = bookingCapacity;
        this.movieName = movieName;
    }
    public String getSlotID() {
        return slotID;
    }

    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }
    public String getmovieName() {
        return movieName;
    }

    public void setmovieName() {this.movieName = movieName;}

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public void setBookingCapacity(int bookingCapacity) {
        this.bookingCapacity = bookingCapacity;
    }

    public int getBookTickets() {
        return bookTickets;
    }

    public void setBookTickets(int bookTickets) {
        this.bookTickets = bookTickets;
    }

    public HashMap<String, Integer> getTickets() {
        return Tickets;
    }

    public void setTickets(HashMap<String, Integer> tickets) {
        Tickets = tickets;
    }


}