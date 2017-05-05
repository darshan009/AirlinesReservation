package com.airlines;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



@RestController
@Transactional
public class FlightController {
    @Autowired //to get the bean called FlightRepository
    private FlightRepository flightRepository;

    @Autowired
    private ReservationRepository reservationRepository;


    private HashMap noFlightFound(String number, String msg){
        HashMap<String,Map> hashMap=new HashMap<String,Map>();
        HashMap<String, String> multiValueMap=new HashMap<String, String>();
        //multiValueMap.put("code","404");
        String code=null;
        String response=null;
        switch (msg) {
            case "not found":
                msg = "Sorry, the requested flight with number " + number + " does not exist";
                code="404";
                response="BadRequest";
                break;
            case "flight deleted":
                msg = "Flight with number " + number + " is deleted successfully";
                code="200";
                response="Response";
                break;
            case "seats error":
                msg = "Flight with number " + number + " cannot be updated as new capacity is less than the existing number of reservations";
                code="400";
                response="Response";
                break;
        }
        multiValueMap.put("code",code);
        multiValueMap.put("msg",msg);//"Sorry, the requested flight with number " + number + " does not exist");
        hashMap.put(response,multiValueMap);
        return hashMap;
    }


    /*
      sort reservation according to output requirements
   */
    private HashMap getSortedFlight(Flight flight){
        HashMap<String, Object> multiValueMap = new HashMap<String, Object>();
        HashMap<String, Object> multiValueMapForFlight = new HashMap<String, Object>();

        multiValueMapForFlight.put("flightNumber", flight.getFlightNumber());
        multiValueMapForFlight.put("price", flight.getPrice());
        multiValueMapForFlight.put("from", flight.getFrom());
        multiValueMapForFlight.put("to", flight.getTo());
        multiValueMapForFlight.put("departureTime", flight.getDepartureTime());
        multiValueMapForFlight.put("arrivalTime", flight.getArrivalTime());
        multiValueMapForFlight.put("description", flight.getDescription());
        multiValueMapForFlight.put("seatsLeft", flight.getSeatsLeft());
        multiValueMapForFlight.put("plane", flight.getPlane());
        multiValueMapForFlight.put("passengers", flight.getPassengers());


        multiValueMap.put("flight", multiValueMapForFlight);

        return multiValueMap;
    }





    //Get flight by flighNumber-return json
    @RequestMapping(path="/flight/{flightNumber}",
                    method = RequestMethod.GET,
                    produces={"application/json"})
    public ResponseEntity getFlight(@PathVariable("flightNumber")String number) {
        try{

            Flight flight = flightRepository.findOne(number);

            if (flight == null)
                return new ResponseEntity(noFlightFound(number,"not found"), HttpStatus.NOT_FOUND);


            //using objectmapper to customize output
            ObjectMapper mapper = new ObjectMapper();

            String flightAsJson = mapper.writeValueAsString(getSortedFlight(flight));
            flightAsJson = flightAsJson.substring(1, flightAsJson.length()-1);


            String fullOutput = "{\"flight\":{"+flightAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity( fullOutput, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }
    }

    /*
     Read flight by ID for xml
   */
    @RequestMapping(path="/flight/{flightNumber}",
            method = RequestMethod.GET,
            params="xml=true",
            produces = {"application/xml"})
    public ResponseEntity getFlight(@PathVariable("flightNumber")String number,
                                  @RequestParam(value="xml") String xml) {

        try {
            Flight flight = flightRepository.findOne(number);

            if (flight == null)
                return new ResponseEntity(noFlightFound(number, "not found"), HttpStatus.NOT_FOUND);


            //using objectmapper to customize output
            ObjectMapper mapper = new ObjectMapper();

            String flightAsJson = mapper.writeValueAsString(getSortedFlight(flight));
            flightAsJson = flightAsJson.substring(1, flightAsJson.length()-1);

            String fullOutput = "{\"flight\":{"+flightAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity(XML.toString(new JSONObject(fullOutput)), HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }

    }

    //Create Flight
    @RequestMapping(path="/flight/{flightNumber}",
                    method = RequestMethod.POST,
                    produces = {"application/xml"})
    public @ResponseBody ResponseEntity flight(@PathVariable("flightNumber") String flightnumber,
                                       @RequestParam(value="price") int price,
                                       @RequestParam(value="from") String from,
                                       @RequestParam(value="to") String to,
                                       @RequestParam(value="departureTime") String departureTime,
                                       @RequestParam(value="arrivalTime") String arrivalTime,
                                       @RequestParam(value="description") String description,
                                       @RequestParam(value="capacity") int capacity,
                                       @RequestParam(value="model") String model,
                                       @RequestParam(value="manufacturer") String manufacturer,
                                       @RequestParam(value="yearOfManufacture") int yearOfManufacture){


        //for date formatting
        DateFormat format = new SimpleDateFormat("yy-dd-MM-hh");

        //create plane and search for flight
        Plane plane=new Plane(capacity,model,manufacturer,yearOfManufacture);
        Flight flight = flightRepository.findOne(flightnumber);
        try {

            //if flight not found create a new one
            if(flight == null) {
                int seatsLeft = capacity;
                flight = new Flight(flightnumber, price, from, to, format.parse(departureTime), format.parse(arrivalTime), seatsLeft, capacity, description);
                flight.setPlane(plane);

            }else{    //if flight found update the existing one

                flight.setPrice(price);
                flight.setArrivalTime( format.parse(arrivalTime) );
                flight.setDepartureTime( format.parse(departureTime) );
                flight.setDescription(description);
                flight.setFrom(from);
                flight.setTo(to);

                //modifying seatsLeft
                //get the new diff add to seats left,new capacity should be greater or equal to the number of reservations
                if( capacity < (flight.getFlightCapacity() - flight.getSeatsLeft()) ){
                    return new ResponseEntity( noFlightFound("seats error", flightnumber), HttpStatus.OK );
                }else{
                    flight.setSeatsLeft( flight.getSeatsLeft() + ( capacity - flight.getFlightCapacity() ) );
                }

                flight.setSeatsLeft(capacity);

            }

            flightRepository.save(flight);

            //using objectmapper to customize output
            ObjectMapper mapper = new ObjectMapper();

            String flightAsJson = mapper.writeValueAsString(getSortedFlight(flight));
            flightAsJson = flightAsJson.substring(1, flightAsJson.length()-1);


            String fullOutput = "{\"flight\":{"+flightAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity( XML.toString(new JSONObject(fullOutput)), HttpStatus.OK);

        }catch(Exception e){

            return new ResponseEntity(e.toString(),HttpStatus.BAD_REQUEST);
        }

    }


    //Delete flight by flight number
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.DELETE)
    public ResponseEntity deleteFlight(@PathVariable("flightNumber")String number) {
        if(flightRepository.findOne(number) == null) {

            return new ResponseEntity(noFlightFound(number,"not found"), HttpStatus.NOT_FOUND);
        }
        else{

            flightRepository.delete(number);
            return new ResponseEntity(noFlightFound(number,"flight deleted"), HttpStatus.OK);

        }
    }


}
