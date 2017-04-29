package com.airlines;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity //indicates that we are using JPA
@Table(name="passenger", uniqueConstraints={@UniqueConstraint(columnNames = {"phone"})})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "reservation")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private Long phone; // Phone numbers must be unique ...

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="reservations")
    private List<Reservation> reservation = new ArrayList<Reservation>();

    public Passenger() { }

    public Passenger(String firstname, String lastname, int age, String gender, Long phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
    }


    public Long getId() {
        return id;
    }

    //getter setter for firstname
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getFirstname() {
        return firstname;
    }

    //getter setter for lastname
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLastname() {
        return lastname;
    }

    //getter setter for age
    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }

    //getter setter for gender
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }

    //getter setter for phone
    public void setPhone(Long phone) {
        this.phone = phone;
    }
    public Long getPhone() {
        return phone;
    }

    //getter setters for reservations
    public void addReservations(Reservation reservation){
        this.reservation.add(reservation);
    }
    public void removeReservations(Reservation reservation){
        this.reservation.remove(reservation);
    }
    public List<Reservation> getReservation(){
        return this.reservation;
    }

}
