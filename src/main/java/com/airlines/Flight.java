package com.airlines;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by darshansapaliga on 4/15/17.
 */

@Entity //indicates that we are using JPA
//@Table(name="flight")
//@SequenceGenerator(name = "seq", allocationSize = 50)
public class Flight {

    //private String number; // Each flight has a unique flight number.
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long flightnumber; // Each flight has a unique flight number.
    private int price;

    //  Date format: yy-mm-dd-hh, do not include minutes and sceonds.
    //Example: 2017-03-22-19
     //  The system only needs to supports PST. You can ignore other time zones.
    @DateTimeFormat(pattern = "yyyy-dd-MM-hh")
    private Date departureTime;
    @DateTimeFormat(pattern = "yyyy-dd-MM-hh")
    private Date arrivalTime;
    private int seatsLeft;
    private String description;
    private String dest_from;
    private String dest_to;
    //private Plane plane;  // Embedded
    //private List<Passenger> passengers;

    //constructors
    public Flight(){ }

    public Flight(int price,String from,String to, Date departureTime, Date arrivalTime, int seatsLeft, String description){
        this.price=price;
        this.dest_from=from;
        this.dest_to=to;
        this.departureTime=departureTime;
        this.arrivalTime=arrivalTime;
        this.seatsLeft=seatsLeft;
        this.description=description;
    }

    //getter setters
    /*public void setFlightNumber(String flightNumber){
        this.number=flightNumber;
    }*/
    public Long getFlightNumber(){
        return this.flightnumber;
    }

    public void setPrice(int price){
        this.price=price;
    }
    public int getPrice(){
        return this.price;
    }

    public void setFrom(String from){
        this.dest_from=from;
    }
    public String getFrom(){
        return this.dest_from;
    }

    public void setTo(String to){
        this.dest_to=to;
    }
    public String getTo(){
        return this.dest_to;
    }

    public void setDepartureTime(Date departureTime){
        this.departureTime=departureTime;
    }
    public Date getDepartureTime(){
        return this.departureTime;
        //return null;
    }

    public void setArrivalTime(Date arrivalTime){
        this.arrivalTime=arrivalTime;
    }
    public Date getArrivalTime(){
        return this.arrivalTime;
        //return null;
    }

    public void setSeatsLeft(int seatsLeft){
        this.seatsLeft=seatsLeft;
    }
    public int getSeatsLeft(){
        return this.seatsLeft;
    }

    public void setDescription(String description){
        this.description=description;
    }
    public String getDescription(){
        return this.description;
    }

}
