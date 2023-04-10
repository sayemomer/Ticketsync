package RM2;



import java.io.IOException;
import java.util.Scanner;

public class Client {
    // private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");

    public static String generateId(String region, String timePeriod, String date) {

        return region.substring(0, 3).toUpperCase() + timePeriod + date;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);


        System.out.println("Enter your UserID: ");
        String x = sc.nextLine();
        try {
            if (x.substring(0, 3).equals("ATW")) {
                System.out.println("we are in Atwater Server");
                BookingInterface obj = null;
                //URL url = new URL("http://localhost:8086/Booking?wsdl");
                //QName qName = new QName("http://Booking/", "BookingImplService");
                //Service service = Service.create(url, qName);

               // obj = service.getPort(BookingInterface.class);


                int num;
                if (x.charAt(3) == 'A') {
                    while (true) {
                        System.out.println("1. Add movie slot.");
                        System.out.println("2. Remove movie slot");
                        System.out.println("3. list of movie shows availability.");
                        System.out.println("4. Book movie tickets.");
                        System.out.println("5. Get booking schedule.");
                        System.out.println("6. Cancel movie tickets.");
                        System.out.println("7. exchange Tickets: ");
                        System.out.println("8. exit");
                        String movieName;
                        String movieID;
                        int BookingCapacity;
                        String customerID;
                        num = Integer.parseInt(sc.nextLine());

                        switch (num) {
                            case 1:
                                String[] movies = {"1.Avatar", "2.Titanic", "3.Avenger"};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(movies[i]);
                                }
                                int y = Integer.parseInt(sc.nextLine());
                                if (y == 1) {
                                    movieName = "Avatar";
                                } else if (y == 2) {
                                    movieName = "Titanic";
                                } else {
                                    movieName = "Avenger";
                                }

                                String[] timeShift = {"M", "A", "E"};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(timeShift[i]);
                                }
                                int z = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter date in ddMMYY: ");
                                String date = sc.nextLine();
                                if (z == 1) {
                                    movieID = generateId("Atwater", "M", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else if (z == 2) {
                                    movieID = generateId("Atwater", "A", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else {
                                    movieID = generateId("Atwater", "E", date);
                                    System.out.println("Movie ID: " + movieID);
                                }
                                System.out.println("Booking Capacity: ");
                                BookingCapacity = Integer.parseInt(sc.nextLine());

                                String xy = obj.addMovieSlots(movieName, movieID, BookingCapacity);
                                System.out.println(xy);

                                break;

                            case 2:
                                System.out.println("Enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();

                                System.out.println(obj.removeMovieSlots(movieID, movieName));
                                break;

                            case 3:
                                System.out.println("enter movieName");
                                movieName = sc.nextLine();
                                String ShowsAvailability = obj.listMovieShowsAvailability(movieName);
                                System.out.println("Shows Available for " + movieName + ": " + ShowsAvailability);
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                int numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 5:
                                customerID = x;
                                System.out.println("enter customerID: " + customerID);
                                String bookingSchedule = obj.getBookingSchedule(customerID);
                                System.out.println("Shows Available for " + customerID + ": " + bookingSchedule);
                                break;
                            case 6:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 7:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 8:
                                break;

                        }
                        if (num == 8) {
                            break;
                        }

                    }

                } else if (x.charAt(3) == 'C') {
                    String movieName;
                    String movieID;
                    int numberOfTickets;
                    while (true) {
                        System.out.println("this is Atwater customer.");
                        System.out.println("1. Book movie tickets.");
                        System.out.println("2. Get booking schedule.");
                        System.out.println("3. Cancel movie tickets.");
                        System.out.println("4. exchange Tickets: ");
                        System.out.println("5. exit");

                        num = Integer.parseInt(sc.nextLine());
                        String customerID = x;
                        switch (num) {
                            case 1:

                                System.out.println("customerID: " + customerID);
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 2:
                                customerID = x;
                                System.out.println(obj.getBookingSchedule(customerID));
                                break;
                            case 3:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 5:
                                break;
                        }
                        if (num == 5) {
                            break;
                        }

                    }
                }

            } else if (x.substring(0, 3).equals("VER")) {
                System.out.println("we are in Verdun Server");
                BookingInterface obj1= null;
               // URL url = new URL("http://localhost:8087/Booking?wsdl");
               // QName qName = new QName("http://Booking/", "BookingImplService");
               // Service service = Service.create(url, qName);

               // obj1 = service.getPort(BookingInterface.class);

                int num;
                if (x.charAt(3) == 'A') {
                    while (true) {
                        System.out.println("1. Add movie slot.");
                        System.out.println("2. Remove movie slot");
                        System.out.println("3. list of movie shows availability.");
                        System.out.println("4. Book movie tickets.");
                        System.out.println("5. Get booking schedule.");
                        System.out.println("6. Cancel movie tickets.");
                        System.out.println("7. exchange Tickets: ");
                        System.out.println("8. exit");
                        String movieName;
                        String movieID;
                        int BookingCapacity;
                        String customerID;
                        num = Integer.parseInt(sc.nextLine());

                        switch (num) {
                            case 1:
                                String[] movies = {"1.Avatar", "2.Titanic", "3.Avenger"};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(movies[i]);
                                }
                                int y = Integer.parseInt(sc.nextLine());
                                if (y == 1) {
                                    movieName = "Avatar";
                                } else if (y == 2) {
                                    movieName = "Titanic";
                                } else {
                                    movieName = "Avenger";
                                }

                                char[] timeShift = {'M', 'A', 'E'};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(timeShift[i]);
                                }
                                int z = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter date in ddMMYY: ");
                                String date = sc.nextLine();
                                if (z == 1) {
                                    movieID = generateId("Verdun", "M", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else if (z == 2) {
                                    movieID = generateId("Verdun", "A", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else {
                                    movieID = generateId("Verdun", "E", date);
                                    System.out.println("Movie ID: " + movieID);
                                }
                                System.out.println("Booking Capacity: ");
                                BookingCapacity = Integer.parseInt(sc.nextLine());

                                String xy = obj1.addMovieSlots(movieName, movieID, BookingCapacity);
                                System.out.println(xy + " with capacity : " + BookingCapacity);

                                break;

                            case 2:
                                System.out.println("Enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();

                                System.out.println(obj1.removeMovieSlots(movieID, movieName));
                                break;

                            case 3:
                                System.out.println("enter movieName");
                                movieName = sc.nextLine();
                                String ShowsAvailability = obj1.listMovieShowsAvailability(movieName);
                                System.out.println("Shows Available for " + movieName + ": " + ShowsAvailability);
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                int numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 5:
                                customerID = x;
                                System.out.println("enter customerID: " + customerID);
                                String bookingSchedule = obj1.getBookingSchedule(customerID);
                                System.out.println("Shows Available for " + customerID + ": " + bookingSchedule);
                                break;
                            case 6:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 7:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 8:
                                break;

                        }
                        if (num == 8) {
                            break;
                        }

                    }

                } else if (x.charAt(3) == 'C') {
                    String movieName;
                    String movieID;
                    int numberOfTickets;
                    while (true) {
                        System.out.println("this is Verdun customer.");
                        System.out.println("1. Book movie tickets.");
                        System.out.println("2. Get booking schedule.");
                        System.out.println("3. Cancel movie tickets.");
                        System.out.println("4. exchange Tickets: ");
                        System.out.println("5. exit");

                        num = Integer.parseInt(sc.nextLine());
                        String customerID = x;
                        switch (num) {
                            case 1:

                                System.out.println("customerID: " + customerID);
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 2:
                                customerID = x;
                                System.out.println(obj1.getBookingSchedule(customerID));
                                break;
                            case 3:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj1.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 5:
                                break;
                        }
                        if (num == 5) {
                            break;
                        }

                    }
                }

            } else if (x.substring(0, 3).equals("OUT")) {
                System.out.println("we are in Outremont Server");
                BookingInterface obj2 = null;
               // URL url = new URL("http://localhost:8088/Booking?wsdl");
               // QName qName = new QName("http://Booking/", "BookingImplService");
              //  Service service = Service.create(url, qName);

              //  obj2 = service.getPort(BookingInterface.class);


                int num;
                if (x.charAt(3) == 'A') {
                    while (true) {
                        System.out.println("1. Add movie slot.");
                        System.out.println("2. Remove movie slot");
                        System.out.println("3. list of movie shows availability.");
                        System.out.println("4. Book movie tickets.");
                        System.out.println("5. Get booking schedule.");
                        System.out.println("6. Cancel movie tickets.");
                        System.out.println("7. exchange Tickets: ");
                        System.out.println("8. exit");
                        String movieName;
                        String movieID;
                        int BookingCapacity;
                        String customerID;
                        num = Integer.parseInt(sc.nextLine());

                        switch (num) {
                            case 1:
                                String[] movies = {"1.Avatar", "2.Titanic", "3.Avenger"};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(movies[i]);
                                }
                                int y = Integer.parseInt(sc.nextLine());
                                if (y == 1) {
                                    movieName = "Avatar";
                                } else if (y == 2) {
                                    movieName = "Titanic";
                                } else {
                                    movieName = "Avenger";
                                }

                                char[] timeShift = {'M', 'A', 'E'};
                                for (int i = 0; i < 3; i++) {
                                    System.out.println(timeShift[i]);
                                }
                                int z = Integer.parseInt(sc.nextLine());
                                System.out.println("Enter date in ddMMYY: ");
                                String date = sc.nextLine();
                                if (z == 1) {
                                    movieID = generateId("Outremont", "M", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else if (z == 2) {
                                    movieID = generateId("Outremont", "A", date);
                                    System.out.println("Movie ID: " + movieID);
                                } else {
                                    movieID = generateId("Outremont", "E", date);
                                    System.out.println("Movie ID: " + movieID);
                                }
                                System.out.println("Booking Capacity: ");
                                BookingCapacity = Integer.parseInt(sc.nextLine());

                                String xy = obj2.addMovieSlots(movieName, movieID, BookingCapacity);
                                System.out.println(xy + " with capacity : " + BookingCapacity);

                                break;

                            case 2:
                                System.out.println("Enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();

                                System.out.println(obj2.removeMovieSlots(movieID, movieName));
                                break;

                            case 3:
                                System.out.println("enter movieName");
                                movieName = sc.nextLine();
                                String ShowsAvailability = obj2.listMovieShowsAvailability(movieName);
                                System.out.println("Shows Available for " + movieName + ": " + ShowsAvailability);
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                int numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 5:
                                customerID = x;
                                System.out.println("enter customerID: " + customerID);
                                String bookingSchedule = obj2.getBookingSchedule(customerID);
                                System.out.println("Shows Available for " + customerID + ": " + bookingSchedule);
                                break;
                            case 6:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 7:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 8:
                                break;

                        }
                        if (num == 8) {
                            break;
                        }

                    }

                } else if (x.charAt(3) == 'C') {
                    String movieName;
                    String movieID;
                    int numberOfTickets;
                    while (true) {
                        System.out.println("this is Outremont customer.");
                        System.out.println("1. Book movie tickets.");
                        System.out.println("2. Get booking schedule.");
                        System.out.println("3. Cancel movie tickets.");
                        System.out.println("4. exchange Tickets: ");
                        System.out.println("5. exit");

                        num = Integer.parseInt(sc.nextLine());
                        String customerID = x;
                        switch (num) {
                            case 1:

                                System.out.println("customerID: " + customerID);
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.bookMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 2:
                                customerID = x;
                                System.out.println(obj2.getBookingSchedule(customerID));
                                break;
                            case 3:
                                customerID = x;
                                System.out.println("enter movieName: ");
                                movieName = sc.nextLine();
                                System.out.println("enter movieID: ");
                                movieID = sc.nextLine();
                                System.out.println("enter number of tickets that you want to cancel: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.cancelMovieTickets(customerID, movieID, movieName, numberOfTickets));
                                break;
                            case 4:
                                customerID = x;
                                System.out.println("enter movieID which you want to exchange: ");
                                movieID = sc.nextLine();
                                System.out.println("enter new movieName: ");
                                String new_movieName = sc.nextLine();
                                System.out.println("enter new movieID: ");
                                String new_movieID = sc.nextLine();
                                System.out.println("Enter number of Tickets: ");
                                numberOfTickets = Integer.parseInt(sc.nextLine());
                                System.out.println(obj2.exchangeTickets(customerID, movieID, new_movieID, new_movieName, numberOfTickets));
                                break;
                            case 5:
                                break;
                        }
                        if (num == 5) {
                            break;
                        }

                    }
                }

            }

        }    catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}