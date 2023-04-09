package Assignment3;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;



public interface RemoteInterface1 {

    String addMovieSlots(String movieName, String movieId, int BookingCapacity);

    String removeMovieSlots(String movieID, String movieName);

    String listMovieShowsAvailability(String movieName) throws IOException;

    String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException;


    String cancelMovieTickets(String customerID,String movieID, String movieName, int numberOfTickets) throws IOException;

    String getBookingSchedule(String customerID) throws IOException;

    String exchangeTickets (String customerID,String movieID, String new_movieID,String new_movieName, int numberOfTickets) throws IOException;

}