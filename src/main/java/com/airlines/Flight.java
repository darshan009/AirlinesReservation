package com.airlines;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;


@Entity //indicates that we are using JPA
public class Flight {

    //private String number; // Each flight has a unique flight number.
    @Id
    private String flightnumber; // Each flight has a unique flight number.
    private int price;

    //  Date format: yy-mm-dd-hh, do not include minutes and sceonds.
    //Example: 2017-03-22-19
     //  The system only needs to supports PST. You can ignore other time zones.
    @DateTimeFormat(pattern = "yy-dd-MM-hh")
    private Date departureTime;
    @DateTimeFormat(pattern = "yy-dd-MM-hh")
    private Date arrivalTime;
    private int seatsLeft;
    private int flightCapacity;
    private String description;
    private String dest_from;
    private String dest_to;


    @Embedded
    public Plane plane;  // Embedded

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(
            name="FLIGHT_PASSENGERS",
            joinColumns={@JoinColumn(name="FLIGHT_NUMBER", referencedColumnName="flightnumber")},
            inverseJoinColumns={@JoinColumn(name="PASSENGER_ID", referencedColumnName="id")})
    private List<Passenger> passengers;



    //constructors
    public Flight(){ }

    public Flight(String flightnumber,int price,String from,String to, Date departureTime, Date arrivalTime, int seatsLeft, int capacity,String description){
        this.flightnumber=flightnumber;
        this.price=price;
        this.dest_from=from;
        this.dest_to=to;
        this.departureTime=departureTime;
        this.arrivalTime=arrivalTime;
        this.seatsLeft=seatsLeft;
        this.flightCapacity=capacity;
        this.description=description;
    }

    //getter setters
    public void setFlightNumber(String flightNumber){
        this.flightnumber=flightNumber;
    }
    public String getFlightNumber(){
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

    public void setFlightCapacity(int flightCapacity){
        this.flightCapacity=flightCapacity;
    }
    public int getFlightCapacity(){
        return this.flightCapacity;
    }

    public void setDescription(String description){
        this.description=description;
    }
    public String getDescription(){
        return this.description;
    }

    public Plane getPlane() {
        return this.plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    //getter setter flights
    public List<Passenger> getPassengers(){
        return this.passengers;
    }
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
    }
    public void removePassenger(Passenger passenger) {
        this.passengers.remove(passenger);
    }


}

