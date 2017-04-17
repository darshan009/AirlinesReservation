package com.airlines;

import javax.persistence.*;


@Entity //indicates that we are using JPA
@Table(name="passenger")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private Long phone; // Phone numbers must be unique ...

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

}
