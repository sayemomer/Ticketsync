package RM4;
import java.time.LocalDate;

public class Booking {
private String bookingID; 
private String movieID;
private String customerID;
private LocalDate bookingDate;
private int bookingCapacity;
private boolean isConfirmed;
public Booking(String bookingID, String movieID, String customerID,LocalDate bookingDate, int bookingCapacity) {
    this.bookingID = bookingID;
    this.movieID = movieID;
    this.customerID = customerID;
    this.bookingCapacity = bookingCapacity;
    this.isConfirmed = true;
    this.bookingDate=bookingDate;
}


public String getBookingID() {
    return bookingID;
}

public String getMovieID() {
    return movieID;
}

public String getCustomerID() {
    return customerID;
}

public int getBookingCapacity() {
    return bookingCapacity;
}
public LocalDate getDate() {
    return bookingDate;
}


public boolean getIsConfirmed() {
    return isConfirmed;
}

public void setIsConfirmed(boolean isConfirmed) {
    this.isConfirmed = isConfirmed;
}
}