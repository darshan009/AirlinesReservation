package com.airlines;

import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.util.*;

import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * Created by nehakumar on 4/19/17.
 */


@RestController
@Transactional
public class FlightController {
   @Autowired //to get the bean called FlightRepository
    private FlightRepository flightRepository;

    private HashMap noFlightFound(){
        HashMap<String,Map> hashMap=new HashMap<String,Map>();
        HashMap<String, String> multiValueMap=new HashMap<String, String>();
        multiValueMap.put("code","404");
        multiValueMap.put("msg","No flight found");
        hashMap.put("Badrequest",multiValueMap);
        return hashMap;
    }

    //Get flight by flighNumber-return json
    //@Token.Consumes("application/json")
    //@Produces("application/json")
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.GET)
    public ResponseEntity getFlight(@PathVariable("flightNumber")String number) {
        try{
            //flightRepository.findOne(Long.valueOf(number));
            if (flightRepository.findOne(number) == null) {

                return new ResponseEntity(noFlightFound(), HttpStatus.NOT_FOUND);
            }
            else {
                return new ResponseEntity(flightRepository.findOne(number), HttpStatus.OK);
            }
        }catch (Exception e){
           return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }
    }


    //Create Flight
    @RequestMapping(path="/flight/{flightNumber}", method = RequestMethod.POST)
    public @ResponseBody Flight flight(@PathVariable("flightNumber") String flightnumber,
                                       @RequestParam(value="price") int price,
                                       @RequestParam(value="dest_from") String dest_from,
                                       @RequestParam(value="dest_from") String dest_to,
                                       @RequestParam(value="departureTime") Date departureTime,
                                       @RequestParam(value="arrivalTime") Date arrivalTime,
                                       @RequestParam(value="seats_left") int seats_left,
                                       @RequestParam(value="description") String description){

        Flight flight = null;
        try {
            flight = new Flight(flightnumber,price,dest_from,dest_to,departureTime,arrivalTime, seats_left, description);
            flightRepository.save(flight);
        }catch(Exception e){
            return null;
        }
        return flight;

    }

    //Update flight
    @RequestMapping(path="/flight/{flightNumber}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity updateFlight(@PathVariable("flightNumber") String flightNumber,
                                       @RequestParam(value="price") int price,
                                       @RequestParam(value="dest_from") String dest_from,
                                       @RequestParam(value="dest_from") String dest_to,
                                       @RequestParam(value="departureTime") Date departureTime,
                                       @RequestParam(value="arrivalTime") Date arrivalTime,
                                      // @RequestParam(value="seats_left") int seats_left,
                                       @RequestParam(value="description") String description){

        Flight flight = null;
        try {

            if(flightRepository.findOne(flightNumber) == null){
                System.out.println("No flight found");
                return new ResponseEntity(noFlightFound(), HttpStatus.NOT_FOUND);
            }

            else {
                flight = flightRepository.findOne(flightNumber);
                flight.setPrice(price);
                flight.setArrivalTime(arrivalTime);
                //flight.setSeatsLeft(seats_left);
                flight.setDepartureTime(departureTime);
                flight.setDescription(description);
                flight.setFrom(dest_from);
                flight.setTo(dest_to);
                flightRepository.save(flight);
                return new ResponseEntity(flightRepository.findOne(flightNumber), HttpStatus.OK);


            }
        }catch(Exception e){

            return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }

    }

    //Delete flight by flight number
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.DELETE)
    public ResponseEntity deleteFlight(@PathVariable("flightNumber")String number) {
        if(flightRepository.findOne(number) == null) {
            //System.out.println("No flight found");
            //return "No flight found";
            return new ResponseEntity(noFlightFound(), HttpStatus.OK);
        }
        else{
            flightRepository.delete(number);
            return new ResponseEntity(flightRepository.findOne(number), HttpStatus.OK);
        }
    }


}
