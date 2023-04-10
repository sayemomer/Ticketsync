package RM4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import com.web.service.impl.OutermontClass;

import ServerInterfaceApp.ServerInterface;
import ServerInterfaceApp.ServerInterfaceHelper;
import com.web.service.impl.*;

import static com.web.service.impl.OutermontClass.movieSlots;





public class OutermontServer {
    public static void main(String[] args) throws Exception {
	
	OutermontClass obj = new OutermontClass();
    DatagramSocket asocket = new DatagramSocket(6992);
    byte[] buffer = new byte[1024];
    DatagramPacket RMsocket = new DatagramPacket(buffer, buffer.length);
    System.out.println("Outermont server is  ready and waiting ...");


    OutermontClass sr = new OutermontClass();
    Runnable task = () -> {
        receive(sr);
    };
    Thread th = new Thread(task);
    th.start();


    while (true) {
        asocket.receive(RMsocket);
        String sentence = new String(RMsocket.getData(), 0, RMsocket.getLength());
        System.out.println("Message Received from RM : " + sentence);
        String[] parts = sentence.split(";");
        String functions = parts[3];
        String response = null;
        if(functions.equalsIgnoreCase("addMovieSlots")){
            String movieName = parts[6];
            String movieID = parts[5];
            String bookCapacity = parts[9];

            System.out.println("movieName: " + movieName);
            System.out.println("movieID: " + movieID);
            System.out.println("Number Of Tickets " + bookCapacity);

            int BookingCapacity = Integer.parseInt(bookCapacity);
            response = obj.addMovieSlots(movieName, movieID, BookingCapacity);
        } else if(functions.equalsIgnoreCase("removeMovieSlots")){
            String movieId = parts[5];
            String movieName = parts[6];
            response = obj.removeMovieSlots(movieId, movieName);
        } else if(functions.equalsIgnoreCase("listMovieShowsAvailability")){
            String movieName = parts[6];
            response = obj.listMovieShowsAvailability(movieName);
        } else if (functions.equalsIgnoreCase("bookMovieTickets")){
            String customerID = parts[4];
            String movieName = parts[6];
            String movieID = parts[5];
            String tickets = parts[9];

            System.out.println("customerID: "+customerID);
            System.out.println("movieName: " + movieName);
            System.out.println("movieID: " + movieID);
            System.out.println("number of tickets: " + tickets);

            int number_of_tickets = Integer.parseInt(tickets);
            response = obj.bookMovieTickets(customerID, movieID,movieName, number_of_tickets);
        }else if(functions.equalsIgnoreCase("getBookingSchedule")){
            String customerID = parts[4];
            System.out.println("customerID : " + customerID);
           response =  obj.getBookingSchedule(customerID);

        }else if(functions.equalsIgnoreCase("cancelMovieTickets")) {
            String customerID = parts[4];
            String movieID = parts[5];
            String movieName = parts[6];
            String numOfTickets = parts[9];
            System.out.println("customerID: " + customerID);
            System.out.println("movieID: " + movieID);
            System.out.println("movieName: " + movieName);
            System.out.println("numberOfTickets: " + numOfTickets);

            int numberOfTickets = Integer.parseInt(numOfTickets);

            response = obj.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets);
        }else if(functions.equalsIgnoreCase("ExchangeTickets")) {
            String customerID = parts[4];
            String new_movieID = parts[5];
            String movieName = parts[6];
            String numOfTickets = parts[9];
            String old_movieName = parts[8];
            
            String old_movieID = parts[7];
            

            System.out.println("customerID: " + customerID);
            System.out.println("movieID: " + new_movieID);
            System.out.println("new_movieName: " + movieName);
            
            System.out.println("old_movieName: " + old_movieName);
            System.out.println("movieID: " + old_movieID);
            System.out.println("old_movieName: " + old_movieName);
            
            System.out.println("numberOfTickets: " + numOfTickets);
            
            int numberOfTickets = Integer.parseInt(numOfTickets);

            int x;
            if (movieSlots.get(movieName).containsKey(new_movieID)) {
                x = movieSlots.get(movieName).get(new_movieID).getBookingCapacity();
            } else {
                x = 0;
            }
            System.out.println(x);
            if (x != 0 && x>numberOfTickets) {
                response = "Available" + " " + movieName + " " + new_movieID;
            } else {
                response = "NotAvailable" + " " + movieName + " " + new_movieID;
            }
        }

            System.out.println(response);
        byte[] b20 = (response + "").getBytes();
        InetAddress ia = InetAddress.getLocalHost();
        DatagramPacket dp5 = new DatagramPacket(b20, b20.length, ia, RMsocket.getPort());
        asocket.send(dp5);
        //Endpoint endpoint = Endpoint.publish("http://localhost:8086/Booking", obj);
        //System.out.println("Booking service is published: " + endpoint.isPublished());


    }
    }




			
//		OutermontClass obj = new OutermontClass();
//		Registry registry = LocateRegistry.createRegistry(2964);
//		
//		 
//         String adminURL = "rmi://localhost:2964/adminATW";
//         Naming.rebind(adminURL,obj);
//
//         CustomerImpl customerObj = new CustomerImpl();
//         String customerURL = "rmi://localhost:2964/customerATW";
//         Naming.rebind(customerURL,customerObj);
//
//		System.out.println("Outermont Server is Started");
//		Runnable task = () -> {
//		
//			receive(obj,customerObj);
//		};
//		Thread thread = new Thread(task);
//		thread.start();
	
	


	private static void receive(OutermontClass obj) {
		DatagramSocket aSocket = null;
	
		String sendingResult = "";
		try {
			aSocket = new DatagramSocket(6666);
			
			byte[] buffer = new byte[1000];
			System.out.println("Outermont UDP Server 6666 Started............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String sentence = new String( request.getData(), 0,
						request.getLength() );
				String[] parts = sentence.split(";");
		
				String function = parts[0]; 
				String userID = parts[1]; 
				String movieID = parts[3]; 
				String movieName = parts[2]; 
				String bookingCapacity = parts[4]; 
				
				if(function.equals("addMovie")) {
					
					String result = obj.addMovieSlots(movieID, movieName,Integer.parseInt(bookingCapacity));
					sendingResult = result;
					sendingResult= sendingResult+";";
				}
				else if(function.equals("listAvailability")) {
					System.out.println(sentence);
					String result = obj.listMovieShowsAvailability(userID).toString();
					sendingResult = result;
					sendingResult= sendingResult+";";
					
				}
				else if(function.equals("book")) {
					String result = obj.bookMovieTickets(userID, movieID, movieName,Integer.parseInt(bookingCapacity));
					sendingResult = result;
					sendingResult= sendingResult+";";
					
				}
				 else if(function.equals("getSchedule")) {
						String result = obj.getBookingSchedule(userID);
						sendingResult = result;
						sendingResult= sendingResult+";";
						
					}
				 else if(function.equals("cancel")) {
						String result = obj.cancelMovieTickets(userID, movieID, movieName,Integer.parseInt(bookingCapacity));
						sendingResult = result.toString();
						sendingResult= sendingResult+";";
						
					} else if(function.equals("exchange")) {
						String function1 = parts[0]; 
						String userID1 = parts[1]; 
						String oldMovieName = parts[2];
						String oldMovieID = parts[3];
						String newMovieID = parts[4];
						String newmovieName = parts[5];
						String bookingCapacity1 = parts[6]; 
						String result = obj.exchangeTickets(userID1, oldMovieID, newMovieID, newmovieName,Integer.parseInt(bookingCapacity1));
						sendingResult = result.toString();
						sendingResult= sendingResult+";";
						
					}

				byte[] sendData = sendingResult.getBytes();
				DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	}
	

