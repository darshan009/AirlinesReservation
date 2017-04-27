package com.airlines;

import org.springframework.transaction.annotation.Propagation;
import javax.transaction.Transactional;
import javax.persistence.Embeddable;

/**
 * Created by darshansapaliga on 4/15/17.
 */
@Embeddable
public class Plane{

    private int capacity;
    private String model;
    private String manufacturer;
    private int yearOfManufacture;


    public Plane(int capacity, String model, String manufacturer, int yearOfManufacture) {
        this.capacity = capacity;
        this.model = model;
        this.manufacturer = manufacturer;
        this.yearOfManufacture = yearOfManufacture;
    }

    public Plane() {

    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return this.model;
    }


    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }


    public int getYearOfManufacture() {
        return this.yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }


    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}

