package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="reservation")
@SequenceGenerator(name = "seq", allocationSize = 50)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private String orderNumber;
    private Passenger passenger;
    private int price; // sum of each flight’s price.
    private List<Flight> flights;

    Flight flight;

    public Reservation(String orderNumber, Passenger passenger, List flights) {

        int totalPrice = 0;
        this.orderNumber = orderNumber;
        this.passenger = passenger;
        this.flights = flights;

        for(Flight flight : this.flights){
            totalPrice += flight.getPrice();
        }
        this.price = totalPrice;
    }

    //getter setter orderNumber
    public String getOrderNumber(){
        return this.orderNumber;
    }
    public void setOrderNumber(String orderNumber){
        this.orderNumber = orderNumber;
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
