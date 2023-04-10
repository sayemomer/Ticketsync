package RM4;

public class Movie {
private String movieID;
private String movieName;
private int bookingCapacity;




public Movie(String movieID, String movieName, int bookingCapacity) {
    this.movieID = movieID;
    this.movieName = movieName;
    this.bookingCapacity = bookingCapacity;
   
}

public String getMovieID() {
    return movieID;
}

public String getMovieName() {
    return movieName;
}

public int getBookingCapacity() {
    return bookingCapacity;
}




public void setBookingCapacity(int bookingcapacity) {
	bookingCapacity=bookingcapacity;	
}


@Override
public String toString() {
    return "movieName=:" + movieName +'\n'+"bookingCapacity:" + bookingCapacity + '\n'+"Date:" + movieID.substring(3,4)+"-"+movieID.substring(4,5)+"-"+movieID.substring(5,6) +'\n' +"Movie Theatre:"+movieID.substring(0,3)=="ATW"?"Atwater":movieID.substring(0,3)=="VER"?"Verdun":"Outermont"+'\n';
}


}
