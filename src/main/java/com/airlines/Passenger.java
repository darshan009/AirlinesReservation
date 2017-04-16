package com.airlines;

/**
 * Created by darshansapaliga on 4/15/17.
 */
public class Passenger {
    private String id;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String phone; // Phone numbers must be unique ...

    public Passenger(String id, String firstname, String lastname, int age, String gender, String phone) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
    }


}
