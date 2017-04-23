package com.airlines;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger", referencedColumnName = "id")
    private Passenger passenger;

    private int price; // sum of each flight’s price../gradle

    @OneToMany(cascade = CascadeType.ALL)
    private List<Flight> flights;

    //returning data does not work without a default constructor
    public Reservation(){}

    public Reservation(Passenger passenger, int price, List flights) {

        int totalPrice = 0;
        this.passenger = passenger;
        this.flights = flights;

        for(Flight flight : this.flights){
            totalPrice += flight.getPrice();
        }
        System.out.println("------------flight total price----------"+totalPrice);
        this.price = price;
    }

    //getter for orderNumber
    public Long getOrderNumber(){
        return this.orderNumber;
    }

    //getter setter flights
    public List getFlights(){
        return this.flights;
    }
    public void setFlights(List flights) {
        this.flights = flights;
    }

    //getter setter price
    public Passenger getPassenger(){
        return this.passenger;
    }
    public void setPassenger(Passenger passenger){
        this.passenger = passenger;
    }

    //getter setter for price
    public int price(){
        return this.price;
    }
    public void setPrice(int price){
        this.price = price;
    }
}
