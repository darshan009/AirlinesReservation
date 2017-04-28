package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@Transactional
public class PassengerController {
    @Autowired //to get the bean called PassengerRepository
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    private HashMap noPassengerFound(Long number, String msg){
        HashMap<String,Map> hashMap = new HashMap<String,Map>();
        HashMap<String, String> multiValueMap = new HashMap<String, String>();

        String code=null;
        String response=null;

        switch (msg) {
            case "not found":
                msg = "Passenger with id " + number + " does not exist";
                code ="404";
                response ="BadRequest";
                break;
            case "passenger deleted":
                msg = "Passenger with number " + number + " is deleted successfully";
                code ="200";
                response ="Response";
                break;
        }

        multiValueMap.put("code",code);
        multiValueMap.put("msg",msg);
        hashMap.put(response,multiValueMap);

        return hashMap;
    }

    /*
        Read passenger by ID and display by JSON
    */
    @RequestMapping(path="/passenger/{id}",
                    method = RequestMethod.GET,
                    produces = {"application/json"})
    public ResponseEntity getPassengerJson(@PathVariable("id")Long id) {

        try {

            Passenger passenger = passengerRepository.findOne(id);

            //check if passenger exists
            if(passenger == null) {
                return new ResponseEntity(noPassengerFound(id, "not found"), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity(passenger, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }


    }

    /*
        Read passenger by ID for xml
    */
    @RequestMapping(path="/passenger/{id}",
            method = RequestMethod.GET,
            params="xml=true",
            produces = {"application/xml"})
    public ResponseEntity getPassenger(@PathVariable("id")Long id,
                                  @RequestParam(value="xml") String xml) {

        try {
            Passenger passenger = passengerRepository.findOne(id);

            //check if passenger exists
            if(passenger == null)
                return new ResponseEntity(noPassengerFound(id, "not found"), HttpStatus.NOT_FOUND);

            return new ResponseEntity(passenger, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Create Passenger
    */
    @RequestMapping(path="/passenger", method = RequestMethod.POST)
    public ResponseEntity passenger(@RequestParam(value="firstname") String firstname,
                               @RequestParam(value="lastname") String lastname,
                               @RequestParam(value="age") int age,
                               @RequestParam(value="gender") String gender,
                               @RequestParam(value="phone") Long phone) {

        Passenger pass = null;
        try {
             pass = new Passenger(firstname, lastname, age, gender, phone);
             passengerRepository.save(pass);

             return new ResponseEntity(pass, HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Update Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.PUT)
    public ResponseEntity updatePassenger(@PathVariable("id")Long id,
                                                @RequestParam(value="firstname") String firstname,
                                                @RequestParam(value="lastname") String lastname,
                                                @RequestParam(value="age") int age,
                                                @RequestParam(value="gender") String gender,
                                                @RequestParam(value="phone") Long phone) {

        System.out.println("in put method");
        Passenger pass = null;
        try {
            pass = passengerRepository.findOne(id);

            //check if passenger exists
            if(pass == null)
                return new ResponseEntity(noPassengerFound(id, "not found"), HttpStatus.NOT_FOUND);

            pass.setFirstname(firstname);
            pass.setLastname(lastname);
            pass.setAge(age);
            pass.setGender(gender);
            pass.setPhone(phone);
            passengerRepository.save(pass);

            return new ResponseEntity(pass, HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Delete Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deletePassenger(@PathVariable("id")Long id) {

        Passenger pass = null;
        try {
            pass = passengerRepository.findOne(id);

            //check if passenger exists
            if(pass == null)
                return new ResponseEntity(noPassengerFound(id, "not found"), HttpStatus.NOT_FOUND);

            //delete/cancel all reservations made by this passenger
            for(Reservation reservation: pass.getReservation()){
                reservationRepository.delete(reservation.getOrderNumber());
            }

            passengerRepository.delete(pass);

            return new ResponseEntity("passenger deleted", HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }

}