package com.airlines;

import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


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
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.GET,produces={"application/json"})
    public ResponseEntity getFlight(@PathVariable("flightNumber")String number) {
        try{
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

    /*
     Read flight by ID for xml
   */
    @RequestMapping(path="/flight/{id}",
            method = RequestMethod.GET,
            params="xml=true",
            produces = {"application/xml"})
    public ResponseEntity getFlight(@PathVariable("flightNumber")String number,
                                  @RequestParam(value="xml") String xml) {
        System.out.println("------------------------xml--------------------------");
        Flight flight = flightRepository.findOne(number);
        if(flight == null)
            return new ResponseEntity(noFlightFound(), HttpStatus.NOT_FOUND);

        return new ResponseEntity(flightRepository.findOne(number), HttpStatus.OK);
    }

    //Create Flight
    @RequestMapping(path="/flight/{flightNumber}", method = RequestMethod.POST)//,params="xml=true", produces = {"application/xml"})
    public @ResponseBody ResponseEntity flight(@PathVariable("flightNumber") String flightnumber,
                                       @RequestParam(value="price") int price,
                                       @RequestParam(value="from") String from,
                                       @RequestParam(value="to") String to,
                                       @RequestParam(value="departureTime") String departureTime,
                                       @RequestParam(value="arrivalTime") String arrivalTime,
                                       //@RequestParam(value="seats_left") int seats_left,
                                       @RequestParam(value="description") String description,
                                       @RequestParam(value="capacity") int capacity,
                                       @RequestParam(value="model") String model,
                                       @RequestParam(value="manufacturer") String manufacturer,
                                       @RequestParam(value="yearOfManufacture") int yearOfManufacture){


        Plane plane=new Plane(capacity,model,manufacturer,yearOfManufacture);
        Flight flight = null;
        try {
                DateFormat format = new SimpleDateFormat("yy-dd-MM-hh");
                flight = new Flight(flightnumber,price,from,to,format.parse(departureTime),format.parse(arrivalTime), capacity,description);
                flight.setPlane(plane);
                flightRepository.save(flight);
        }catch(Exception e){

            return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(flight, HttpStatus.OK);

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
