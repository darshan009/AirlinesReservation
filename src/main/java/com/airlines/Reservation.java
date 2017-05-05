package com.airlines;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reservation")
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "passenger")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderNumber;

    private int price; // sum of each flight’s price../gradle

    //(cascade = CascadeType.ALL) causes it to delete the mapped parent field/child field as well

    @OneToOne
    @JoinColumn(name = "passenger", referencedColumnName = "id")
    private Passenger passenger;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(
            name="T_RESERVATION_FLIGHT",
            joinColumns={@JoinColumn(name="RESERVATION_NO", referencedColumnName="orderNumber")},
            inverseJoinColumns={@JoinColumn(name="FLIGHT_ID", referencedColumnName="flightnumber")})
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
