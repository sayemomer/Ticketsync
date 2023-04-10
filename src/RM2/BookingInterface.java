package RM2;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.IOException;


@WebService
@SOAPBinding(style=Style.RPC)
public interface BookingInterface {
    @WebMethod
    String addMovieSlots(String movieName, String movieId, int BookingCapacity);
    @WebMethod
    String removeMovieSlots(String movieID, String movieName);
    @WebMethod
    String listMovieShowsAvailability(String movieName) throws IOException;
    @WebMethod
    String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException;

    @WebMethod
    String cancelMovieTickets(String customerID,String movieID, String movieName, int numberOfTickets) throws IOException;
    @WebMethod
    String getBookingSchedule(String customerID) throws IOException;
    @WebMethod
    String exchangeTickets (String customerID,String movieID, String new_movieID,String new_movieName, int numberOfTickets) throws IOException;

}