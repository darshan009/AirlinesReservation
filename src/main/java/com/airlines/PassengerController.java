package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class PassengerController {
    @Autowired //to get the bean called PassengerRepository

    private PassengerRepository passengerRepository;

    /*
        Read passenger by ID
    */
    @RequestMapping(path="/passenger/{id}")
    public Passenger getPassenger(@PathVariable("id")Long id, @RequestParam(value="json") String json) {
        if(passengerRepository.findOne(id) == null)
            System.out.println("No passengers found");
        return passengerRepository.findOne(id);
    }

    /*
        Create Passenger
    */
    @RequestMapping(path="/passenger", method = RequestMethod.POST)
    public @ResponseBody Passenger passenger(@RequestParam(value="firstname") String firstname,
                               @RequestParam(value="lastname") String lastname,
                               @RequestParam(value="age") int age,
                               @RequestParam(value="gender") String gender,
                               @RequestParam(value="phone") Long phone) {

        Passenger pass = null;
        try {
             pass = new Passenger(firstname, lastname, age, gender, phone);
             passengerRepository.save(pass);
        }catch(Exception e){
            System.out.println("Error in creating new passenger "+pass);
            return null;
        }

        return pass;

    }

    /*
        Update Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.PUT)
    public @ResponseBody Passenger updatePassenger(@PathVariable("id")Long id,
                                                @RequestParam(value="firstname") String firstname,
                                                @RequestParam(value="lastname") String lastname,
                                                @RequestParam(value="age") int age,
                                                @RequestParam(value="gender") String gender,
                                                @RequestParam(value="phone") Long phone) {

        System.out.println("in put method");
        Passenger pass = null;
        try {
            pass = passengerRepository.findOne(id);
            pass.setFirstname(firstname);
            pass.setLastname(lastname);
            pass.setAge(age);
            pass.setGender(gender);
            pass.setPhone(phone);
            passengerRepository.save(pass);

        }catch(Exception e){
            return null;
        }

        return pass;

    }

    /*
        Delete Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.DELETE)
    public @ResponseBody String deletePassenger(@PathVariable("id")Long id) {

        Passenger pass = null;
        try {
            pass = passengerRepository.findOne(id);
            passengerRepository.delete(pass);

        }catch(Exception e){
            return "Error deleting user"+e.toString();
        }

        return "User successfully deleted!";

    }

}