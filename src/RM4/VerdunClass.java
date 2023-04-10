package RM4;
import java.rmi.*;

import java.rmi.server.*;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.swing.AbstractButton;

import javax.swing.ButtonGroup;

import javax.swing.JOptionPane;





import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class VerdunClass implements RemoteInterface{
private static final long serialVersionUID = 1L;
  
    public static HashMap<String, HashMap<String, Movie>> movieSlots;
	private HashMap<String, Booking> bookings;
	private String theatrePrefix = "ver";
	private int atwaterServerPortNumber = 8888;
	private int verdunServerPortNumber = 7777;
	private int outermontServerPortNumber = 6666;
    public VerdunClass() throws RemoteException {
		super();
		 HashMap<String, HashMap<String, Movie>> movieSlot=new HashMap<>();
		 HashMap<String, Movie> movie1=new HashMap<>();;
		 HashMap<String, Movie> movie2=new HashMap<>();;
		 HashMap<String, Movie> movie3=new HashMap<>();;
		 HashMap<String, Booking> booking=new HashMap<>();
		 movie1.put("VERM220323",new Movie("VERM220323","Avatar",25));
		 movie2.put("VERA220323",new Movie("VERA220323","Avengers",25));
		 movie3.put("VERE220323",new Movie("VERE220323","Titanic",25));
		movieSlot.put("Avatar",movie1);
		movieSlot.put("Avengers",movie2);
		movieSlot.put("Titanic", movie3);
		this.movieSlots=movieSlot;
		this.bookings=booking;
		
	}
	
    public static  String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            System.out.println(button.getText());
            if (button.isSelected()) {
            	System.out.println("selected" +button.getText());
                return button.getText();
            }
        }

        return null;
    }
    public String addMovieSlots(String movieID, String movieName, int bookingCapacity)  {
		String moviePrefix = movieID.substring(0, Math.min(movieID.length(), 3)).toLowerCase();
//		String userPrefix = userID.substring(0, Math.min(userID.length(), 3)).toLowerCase();
//		String userType = userID.substring(3, Math.min(userID.length(), 4));
        HashMap<String, Movie> movie = movieSlots.get(movieName);
//	if(moviePrefix.equals(userPrefix) && (userType.equals("A") || userType.equals("a"))){
			  if (movie == null) {
					System.out.println("1");
		            movie = new HashMap<>();
		            movieSlots.put(movieName, movie);
		            String action = "ADD ITEM "+ movieID;
		            
		            return "Movie Added";
		        }
		
			if(movie.containsKey(movieID)) {
				System.out.println("2");
				movie.get(movieID).setBookingCapacity(movie.get(movieID).getBookingCapacity()+ bookingCapacity);	String action = "ADD ITEM "+ movieID;
				return "Movie Added";
			}else {
				System.out.println("3");
				movie.put(movieID, new Movie(movieID, movieName, bookingCapacity));
				movieSlots.put(movieName,movie);
				String action = "ADD ITEM "+ movieID;
				return "Movie Added"; 
			
			}

    } 
    
    
	@Override
	public String removeMovieSlots(String movieID, String movieName) {
	
		String moviePrefix = movieID.substring(0, Math.min(movieID.length(), 3)).toLowerCase();

		
        HashMap<String, Movie> movie = movieSlots.get(movieName);
		
			
			if (movie.containsKey(movieID)) {
				 // Delete movie and book next available movie show for clients
		        movie.remove(movieID);
	        	String action = "Remove Movie "+ movieID;   	
	           return "Movie Removed";
	        }
        if (!movie.containsKey(movieID)) {
            // Movie slot does not exist
        	String action = "Remove Movie "+ movieID;
     
           return "Movie Slots Could not be removed";
        }
   return "Movie Slots Could not be removed";
		
    }
    




@Override
public String listMovieShowsAvailability(String movieName) throws IOException {

		String itemList = "";
		 HashMap<String, Movie> movie = movieSlots.get(movieName);
		for (Map.Entry<String, Movie> entry : movie.entrySet()) {
			System.out.println(movieName);
			
			String name = entry.getValue().getMovieName();
			if(name.equalsIgnoreCase(movieName)) {
				String action = "List movie availability: "+movieName;
	
				
				itemList = itemList+entry.getValue().getMovieID() +' ' + entry.getValue().getBookingCapacity()+'\n';
				System.out.println("-----"+itemList);
				
			}

		}
	
		
			if(theatrePrefix.equals("atw")) {
			String resultOut = sendMessage(outermontServerPortNumber,"listAvailability", "", movieName,  null,  0);
				String resultVer = sendMessage(verdunServerPortNumber,"listAvailability", "", movieName,  null,  0);
				itemList = itemList+resultOut+resultVer;
				System.out.println("list"+itemList);
				System.out.println("M"+resultOut);
				System.out.println("L"+resultVer);
			}else if(theatrePrefix.equals("ver")) {
				String resultAtw = sendMessage(atwaterServerPortNumber,"listAvailability", "", movieName,  null,  0);
				String resultOut = sendMessage(outermontServerPortNumber,"listAvailability", "", movieName,  null,  0);
				itemList = itemList+resultAtw+resultOut;
				System.out.println("list"+itemList);
			}else if(theatrePrefix.equals("out")) {
				String resultAtw = sendMessage(atwaterServerPortNumber,"listAvailability", "", movieName,  null,  0);
				String resultVer = sendMessage(verdunServerPortNumber,"listAvailability", "", movieName,  null,  0);				
				itemList = itemList+resultVer+resultAtw;
				System.out.println("list"+itemList);
			}
			
		

		return itemList;

    }
@Override
public String bookMovieTickets(String customerID, String movieID, String movieName, int tickets)
		throws IOException {

		 // Check if customer belongs to the same area as the movie
		 String customerArea = customerID.substring(0, 4).toLowerCase();
		 String movieArea = movieID.substring(0, 3).toLowerCase();
		 HashMap<String, Movie> movie = movieSlots.get(movieName);
		 System.out.println("------------"+movieArea);
if(theatrePrefix.toLowerCase().equals(movieArea)) {
		 // Check if customer has booked more than 3 movies from other areas in a week
		 LocalDate today = LocalDate.now();
		 LocalDate weekAgo = today.minusWeeks(1);
		 int otherAreaBookings = 0;
		 for (Map.Entry<String, Booking> entry : bookings.entrySet()) {
		 if (!entry.getValue().getCustomerID().substring(0, 4).equals(customerArea)
		 && entry.getValue().getDate().isAfter(weekAgo)
		 && entry.getValue().getDate().isBefore(today)) {
		 otherAreaBookings++;
		 }
		 if (otherAreaBookings >= 3) {
		 return "Cannot book more than 3 movies from other areas in a week";
		 }
		 }
		 
		 // Check if the movie exists and if there are enough tickets available
		 if (!movie.containsKey(movieID)) {
		 return "Movie show does not exist";
		 }
			
		if(movie.containsKey(movieID)) {
		 if (movie.get(movieID).getBookingCapacity() < tickets) {
		 return "Not enough tickets available";
		 }}
		 // Check if customer has already booked a ticket for the same movie and show
		 for (Map.Entry<String, Booking> entry : bookings.entrySet()) {
		 if (entry.getValue().getCustomerID().equals(customerID)
		 && entry.getValue().getMovieID().equals(movieID)) {
		 return "Cannot book multiple tickets for the same movie and show";
		 }
		 }
		 
		 if (movie.containsKey(movieID)) {
		 movie.get(movieID).setBookingCapacity(movie.get(movieID).getBookingCapacity() - tickets);
		 UUID uuid = UUID.randomUUID();
	        String uuidAsString = uuid.toString();
		 String bookingID = customerArea + movieArea +uuidAsString;
		 Booking booking = new Booking(bookingID, movieID, customerID, LocalDate.now(), tickets);
		 System.out.println(bookingID);
		 bookings.put(bookingID, booking);
return "Booking Succesful";
		}
		 // Log the booking
		 try {
			 
		 FileWriter fw = new FileWriter(customerID + ".txt", true);
		 BufferedWriter bw = new BufferedWriter(fw);
		 bw.write("Booked movie ticket - " + movieID + " - " + LocalDate.now().toString() + " - " + tickets + " tickets");
		 bw.newLine();
		 bw.close();
		 
		 } catch (IOException e) {
		 e.printStackTrace();
		 }
		 
			
	 }
		else if(movieArea.equals("atw")) {
				String result = sendMessage(atwaterServerPortNumber,"book", customerID, movieName,  movieID,  tickets);
				System.out.println(result);
				return result;
			}else if(movieArea.equals("out")) {
				String result = sendMessage(outermontServerPortNumber,"book", customerID, movieName,  movieID,  tickets);
				return result;
			}else if(movieArea.equals("ver")) {
				String result = sendMessage(verdunServerPortNumber,"book", customerID, movieName,  movieID,  tickets);
				return result;
			}
		
			
					String action = "Book tickets: "+movieID;
					try {
						logCreate(customerID, action, "false");

						serverLogCreate(customerID,action,"false", "Failed",  "USER ID: "+customerID+"/ movie ID: "+movieID+"/ tickets: "+ tickets);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "Booking failed";
				
			
			
		 }
	 @Override
	public String cancelMovieTickets(String customerID, String movieID,String movieName,int tickets)  {
		// Check if customer belongs to the same area as the movie
		 String customerArea = customerID.substring(0, 4).toLowerCase();
		 String movieArea = movieID.substring(0, 3).toLowerCase();
		 HashMap<String, Movie> movie = movieSlots.get(movieName);
		 if (!customerArea.equals(theatrePrefix)) {
	
		 for (Map.Entry<String, Booking> entry : bookings.entrySet()) {
		 if (entry.getValue().getCustomerID().equals(customerID)){ 
		 movie.get(movieID).setBookingCapacity(movie.get(movieID).getBookingCapacity() + tickets);
		 String bookingID = customerArea + LocalDate.now().toString().replace("-", "");
		 bookings.remove(entry.getKey());
		 
		 String action = "Cancel tickets: "+movieID;
		
		 // Log 
		 try {
				logCreate(customerID, action, "Success");

				serverLogCreate(customerID,action,"true", "Success",  "USER ID: "+customerID+"/ movie ID: "+movieID+"/ tickets: "+ tickets);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return "Tickets Cancelled";
		 
		 }
		 
		 }}
				
			
					String action = "Cancel tickets: "+movieID;
					try {
						logCreate(customerID, action, "false");

						serverLogCreate(customerID,action,"false", "Failed",  "USER ID: "+customerID+"/ movie ID: "+movieID+"/ tickets: "+ tickets);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "Ticket cancellation failed";
				
	}

	
	public String getBookingSchedule(String customerID)  {
		String itemList = "";
		
		String userPrefix = customerID.substring(0, Math.min(customerID.length(), 3)).toLowerCase();
		String userType = customerID.substring(3, Math.min(customerID.length(), 4));
	
			System.out.println("-----"+bookings.size());
			for (Map.Entry<String, Booking> entry : bookings.entrySet()) {
				System.out.println("hhhh");
				String name = entry.getValue().getCustomerID();
				if(name.equalsIgnoreCase(customerID)) {
					String action = "Movie Schedule of customer";
					try {
						logCreate(customerID, action, String.valueOf(entry));
						serverLogCreate(customerID,action, String.valueOf(entry), "Success",  "USER ID: "+customerID+"/ Movie Name: "+entry.getValue().getCustomerID());
					} catch (IOException e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					itemList = itemList+itemList==""?"":":"+ "movieID:"+entry.getValue().getMovieID()+"\n"+"booking Capacity"+ entry.getValue().getBookingCapacity();
					System.out.println("-----"+itemList);
					
				}

			}
			if(userPrefix.equals(theatrePrefix)) {
				System.out.println("kkkkkkkkkk");
				if(theatrePrefix.equals("atw")) {
				String resultOut = sendMessage(outermontServerPortNumber,"getSchedule", customerID, "",  null,  0);
					String resultVer = sendMessage(verdunServerPortNumber,"getSchedule", customerID, "",  null,  0);
					itemList = itemList+resultOut+resultVer;
					System.out.println("list"+itemList);
					System.out.println("M"+resultOut);
					System.out.println("L"+resultVer);
				}else if(theatrePrefix.equals("ver")) {
					String resultAtw =sendMessage(atwaterServerPortNumber,"getSchedule", customerID, "",  null,  0);
					String resultOut = sendMessage(outermontServerPortNumber,"getSchedule", customerID, "",  null,  0);
					itemList = itemList+resultAtw+resultOut;
					System.out.println("list"+itemList);
				}else if(theatrePrefix.equals("out")) {
					String resultAtw = sendMessage(atwaterServerPortNumber,"getSchedule", customerID, "",  null,  0);
					String resultVer = sendMessage(verdunServerPortNumber,"getSchedule", customerID, "",  null,  0);
					itemList = itemList+resultVer+resultAtw;
					System.out.println("list"+itemList);
				}
				
			}
			System.out.println(itemList);
			return itemList;
	
	}
	
	
	
	
    private void log(String string, String movieID, String movieName,String extra) {
    	
		Logger logger = Logger.getLogger("MovieTicketBookingLog");
		logger.setLevel(Level.INFO);
		FileHandler fileHandler = null;
		try {
		fileHandler = new FileHandler("MovieTicketBooking.log");
		logger.addHandler(fileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		fileHandler.setFormatter(formatter);
		} catch (IOException e) {
		e.printStackTrace();
		}	
}
    public static boolean login() {
    	String userID = JOptionPane.showInputDialog
    			(null, "Please enter your user name", "Input", JOptionPane.QUESTION_MESSAGE );

    		String password = JOptionPane.showInputDialog
    			(null, "Please enter your password", "Input", JOptionPane.QUESTION_MESSAGE );

    		if(userID.equals("admin") && password.equals("open"))
    		{
    			JOptionPane.showMessageDialog
    			(null, "welcome", "Administrative Logon", JOptionPane.PLAIN_MESSAGE );
    			return true;
    		}
    		else if(userID.equals("admin") & !(password.equals("open")))
    		{
    			JOptionPane.showMessageDialog
    			(null, "invalid password", "Administrative Logon", JOptionPane.ERROR_MESSAGE);
    		}
    		else if(!(userID.equals("admin")) & password.equals("open"))
    		{
    			JOptionPane.showMessageDialog
    			(null, "invalid user id", "Administrative Logon", JOptionPane.ERROR_MESSAGE);
    		}
    		else
    		    JOptionPane.showMessageDialog
    			(null, "invalid user id and password", "Administrative Logon", JOptionPane.ERROR_MESSAGE);
			return false;

  
        
    }
	public void logCreate(String userID, String acion, String response) throws IOException {
		String userPrefix = userID.substring(0, Math.min(userID.length(), 3)).toLowerCase();
		final String dir = System.getProperty("user.dir");
		String fileName = dir;
		if(userPrefix.equals("atw")) {
			fileName = dir+"\\src\\Log\\Client\\Atwater\\"+userID+".txt";
		}else if(userPrefix.equals("out")) 
		{
			fileName = dir+"\\src\\Log\\Client\\Outermont\\"+userID+".txt";
		}else if(userPrefix.equals("ver")) 
		{
			fileName = dir+"\\src\\Log\\Client\\Verdun\\"+userID+".txt";
		}


		FileWriter fileWriter = new FileWriter(fileName,true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println("Action: "+acion+" | Resonse: "+ response);

		printWriter.close();

	}


	public void serverLogCreate(String userID, String acion, String response,String requestResult, String peram) throws IOException {
		String userPrefix = userID.substring(0, Math.min(userID.length(), 3)).toLowerCase();
		final String dir = System.getProperty("user.dir");
		String fileName = dir;
		if(userPrefix.equals("atw")) {
			fileName = dir+"\\src\\Log\\Server\\Atwater.txt";
		}else if(userPrefix.equals("out")) 
		{
			fileName = dir+"\\src\\Log\\Server\\Outermont.txt";
		}else if(userPrefix.equals("ver")) 
		{
			fileName = dir+"\\src\\Log\\Server\\Verdun.txt";
		}

		Date date = new Date();

		String strDateFormat = "yyyy-MM-dd hh:mm:ss a";

		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

		String formattedDate= dateFormat.format(date);


		FileWriter fileWriter = new FileWriter(fileName,true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println("DATE: "+formattedDate+"Action: "+acion+" | Parameters: "+ peram +" | Action Status: "+requestResult+" | Resonse: "+ response);

		printWriter.close();

	}
	private static String sendMessage(int serverPort,String function,String userID,String movieName, String movieId, int numberOfTickets) {
		DatagramSocket aSocket = null;
		String result ="";
		String dataFromClient = function+";"+userID+";"+movieName+";"+movieId+";"+numberOfTickets;
		try {
			aSocket = new DatagramSocket();
			byte[] message = dataFromClient.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
			aSocket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			System.out.println("IO: " + result);
			String[] parts = result.split(";");
			result = parts[0];
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return result;

	}


	@Override
	public String exchangeTickets(String customerID, String oldMovieID,String newMovieID, String newMovieName,
			int numberOfTickets)  throws IOException {
		boolean newBookingAvailable = false;
		boolean oldBookingAvailable =false;
		boolean checkAlreadyBorrowed = true;
		
		String itemPrefix = newMovieID.substring(0, Math.min(newMovieID.length(), 3)).toLowerCase();
		String oldMovieIDPrefix = oldMovieID.substring(0, Math.min(oldMovieID.length(), 3)).toLowerCase();
		System.out.println(oldMovieID+"ppppppppppppppp");
		System.out.println("++++++++++++________"+newMovieID);
//		String userPrefix = customerID.substring(0, Math.min(customerID.length(), 3)).toLowerCase();
		String userType = customerID.substring(3, Math.min(customerID.length(), 4));
	       String targetValue = oldMovieID;
	        String oldMovieName = null;

	        for (Map.Entry<String,  HashMap<String, Movie>> entry : movieSlots.entrySet()) {

	            String x = String.valueOf(entry.getValue());
	            System.out.println(x);
	            if (x.contains(targetValue)) {
	                oldMovieName = entry.getKey();
	                System.out.println("if condition enter");
	                System.out.println(oldMovieName);
	                break;
	            }
	        }
	        System.out.println("old movieName is " +oldMovieName);
        String swapAppointmentMessage = "";
        if(oldMovieID.contains("OUT")){
        	String result = sendMessageExchange(outermontServerPortNumber,"exchange", customerID,  oldMovieID,  oldMovieName, newMovieID,  newMovieName,numberOfTickets);
			System.out.println(result);
        }
        else if(oldMovieID.contains("VER")){
        	String result = sendMessageExchange(verdunServerPortNumber,"exchange", customerID,  oldMovieID,  oldMovieName, newMovieID,  newMovieName,numberOfTickets);
			System.out.println(result);
        }
        else {
        	if(movieSlots.containsKey(oldMovieName)) {
				HashMap<String, Movie> oldMovie = movieSlots.get(oldMovieName);
				HashMap<String, Movie> newMovie = movieSlots.get(newMovieName);
				for (Map.Entry<String, Movie> entry : oldMovie.entrySet()) {
					if(entry.getValue().getMovieID().equals(oldMovieID)){
					   
	                      if(newMovieID.contains("OUT")){
	                    		String result = sendMessage(outermontServerPortNumber,"book", customerID, newMovieName,  newMovieID,  numberOfTickets);
	            				System.out.println(result);
	                            if(result.equals("Booking failed")){
	                                swapAppointmentMessage = "Cannot swap appointment! Try again later";
	                                try {
                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
                					} catch (IOException e) {

                						// TODO Auto-generated catch block
                						e.printStackTrace();
                					}
	                                
	                            }
	                            else if(result.equals("Booking Succesful")){
	                                //cancel appointment from MTL
	                                if(cancelMovieTickets(customerID, oldMovieID,oldMovieName,numberOfTickets).equals("Tickets Cancelled")){
	                                   
	                                    
	                                    swapAppointmentMessage = "Appointment swapped successfully!";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                   
	                                }
	                                else {
	                                    swapAppointmentMessage = "Cannot swap appointment! Try again later";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                    
	                                }
	                            }
	                        }
	                        else if(newMovieID.contains("VER")){
	                    		String result = sendMessage(verdunServerPortNumber,"book", customerID, newMovieName,  newMovieID,  numberOfTickets);
	            				System.out.println(result);
	                            if(result.equals("Booking failed")){
	                                swapAppointmentMessage = "Cannot swap appointment! Try again later";
	                                
	                            }
	                            else if(result.equals("Booking Succesful")){
	                                //cancel appointment from MTL
	                                if(cancelMovieTickets(customerID, oldMovieID,oldMovieName,numberOfTickets).equals("Tickets Cancelled")){
	                                   
	                                    
	                                    swapAppointmentMessage = "Appointment swapped successfully!";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                   
	                                }
	                                else {
	                                    swapAppointmentMessage = "Cannot swap appointment! Try again later";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                    
	                                }
	                            }
	                        }
	                        else if(newMovieID.contains("ATW")){
	                            if(checkAvailability(numberOfTickets, newMovieID,newMovieName)){
	                                if(cancelMovieTickets(customerID, oldMovieID,oldMovieName,numberOfTickets).equals("Tickets Cancelled")){
	                                    bookMovieTickets(customerID, newMovieID,  newMovieName,numberOfTickets);
	                                    swapAppointmentMessage = "Appointment swapped successfully!";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                    
	                                }
	                                else {
	                                    swapAppointmentMessage = "Cannot swap appointment! Try again later";
	                                    try {
	                						logCreate(customerID, swapAppointmentMessage, String.valueOf(entry));
	                						serverLogCreate(customerID,swapAppointmentMessage,"","","");
	                					} catch (IOException e) {

	                						// TODO Auto-generated catch block
	                						e.printStackTrace();
	                					}
	                                    
	                                }
	                            }
	                        }
	                    }
					
				}}
        }
        return swapAppointmentMessage;
    }

	private boolean checkAvailability(int numberOfTickets, String newMovieID,String movieName) {
		
		if(movieSlots.containsKey(movieName)) {
			
			HashMap<String, Movie> newMovie = movieSlots.get(movieName);
			for (Map.Entry<String, Movie> entry : newMovie.entrySet()) {
				if(entry.getValue().getBookingCapacity()>numberOfTickets) {
					return true;
				}
			}}
       
		return false;
	}
	
	

	private String sendMessageExchange(int serverPort, String function, String customerID,
			String oldMovieID, String oldMovieName, String newMovieID, String newMovieName,int numberOfTickets) {
		DatagramSocket aSocket = null;
		String result ="";
		String dataFromClient = function+";"+customerID+";"+oldMovieName+";"+oldMovieID+";"+newMovieID+";"+newMovieName+";"+numberOfTickets;
		try {
			aSocket = new DatagramSocket();
			byte[] message = dataFromClient.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
			aSocket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			result = new String(reply.getData());
			System.out.println("IO: " + result);
			String[] parts = result.split(";");
			result = parts[0];
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return result;
	}
	}