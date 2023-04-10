package RM4;

import java.io.IOException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


	public interface RemoteInterface {

	    String addMovieSlots(String movieName, String movieId, int BookingCapacity);

	    String removeMovieSlots(String movieID, String movieName);

	    String listMovieShowsAvailability(String movieName) throws IOException;

	    String bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) throws IOException;


	    String cancelMovieTickets(String customerID,String movieID, String movieName, int numberOfTickets) throws IOException;

	    String getBookingSchedule(String customerID) throws IOException;

	    String exchangeTickets (String customerID,String movieID, String new_movieID,String new_movieName, int numberOfTickets) throws IOException;

	}


