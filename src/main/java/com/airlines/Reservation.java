package com.airlines;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reservation")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderNumber;

    private int price; // sum of each flight’s price../gradle

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger", referencedColumnName = "id")
    private Passenger passenger;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="flights")
    private List<Flight> flights = new ArrayList<Flight>();

    //returning data does not work without a default constructor
    public Reservation(){}

    public Reservation(Passenger passenger, List<Flight> flights) {

        int totalPrice = 0;
        this.passenger = passenger;
        this.flights = flights;

        for(Flight flight : this.flights){
            totalPrice += flight.getPrice();
        }
        System.out.println("------------flight total price----------"+totalPrice);
        this.price = totalPrice;
    }

    //getter for orderNumber
    public Long getOrderNumber(){
        return this.orderNumber;
    }

    //getter setter for price
    public int getPrice(){
        return this.price;
    }
    public void setPrice(int price){
        this.price = price;
    }

    //getter setter flights
    public List<Flight> getFlights(){
        return this.flights;
    }
    public void setFlights(List flights) {
        this.flights = flights;
    }

    //getter setter passenger
    public Passenger getPassenger(){
        return this.passenger;
    }
    public void setPassenger(Passenger passenger){
        this.passenger = passenger;
    }

}
