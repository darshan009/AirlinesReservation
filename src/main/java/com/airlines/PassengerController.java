package com.airlines;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Transactional
public class PassengerController {
    @Autowired //to get the bean called PassengerRepository
    private PassengerRepository passengerRepository;

    @Autowired
    private ReservationRepository reservationRepository;



    /*
        Error json format display
     */
    private HashMap noPassengerFound(Long number, String msg, String err){
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
            case "error reading passenger":
                msg = "Error in reading passenger "+err;
                code ="400";
                response ="BadRequest";
                break;
            case "error creating passenger":
                msg = "Error in creating new passenger "+err;
                code ="400";
                response ="BadRequest";
                break;
            case "error updating passenger":
                msg = "Error in updating new passenger "+err;
                code ="400";
                response ="BadRequest";
                break;
            case "error deleting passenger":
                msg = "Error in deleting new passenger "+err;
                code ="400";
                response ="BadRequest";
                break;
            case "passenger deleted":
                code ="200";
                msg = "Passenger with number " + number + " is deleted successfully";
                response ="Response";
                break;
        }

        multiValueMap.put("code",code);
        multiValueMap.put("msg",msg);
        hashMap.put(response,multiValueMap);

        return hashMap;
    }


    /*
        sort reservation according to output requirements
     */
    private HashMap getSortedReservation(Reservation reservation){
        HashMap<String, Object> multiValueMap = new HashMap<String, Object>();
        HashMap<String, Object> multiValueMapForIndividualFlights = new HashMap<String, Object>();
        List<HashMap> multiValueMapForFlights = new ArrayList<HashMap>();
        HashMap<String, Object> multiValueMapFixForFlights = new HashMap<String, Object>();

        //for date formatting
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH");

        //orderNumber
        multiValueMap.put("orderNumber", (reservation.getOrderNumber()).toString());

        //price
        multiValueMap.put("price", String.valueOf(reservation.getPrice()) );

        //customize flight fields and then add
        List<Flight> flights = reservation.getFlights();

        //iterate through all flights and get specific fields
        for(Flight flight: flights){

            multiValueMapForIndividualFlights.put("number", flight.getFlightNumber());
            multiValueMapForIndividualFlights.put("price", flight.getPrice());
            multiValueMapForIndividualFlights.put("from", flight.getFrom());
            multiValueMapForIndividualFlights.put("to", flight.getTo());
            multiValueMapForIndividualFlights.put("departureTime", formatDate.format(flight.getDepartureTime()) );
            multiValueMapForIndividualFlights.put("arrivalTime", formatDate.format(flight.getArrivalTime()) );
            multiValueMapForIndividualFlights.put("description", flight.getDescription());

            multiValueMapForFlights.add(multiValueMapForIndividualFlights);

        }

        multiValueMapFixForFlights.put("flight", multiValueMapForFlights);

        multiValueMap.put("flights",  multiValueMapFixForFlights);

        return multiValueMap;
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
                return new ResponseEntity(noPassengerFound(id, "not found", null), HttpStatus.NOT_FOUND);
            }

            //if passenger found get reservations
            List<Reservation> reservation = reservationRepository.findByPassenger(passenger);

            //get all reservations formatted
            HashMap<String, Object> reservationsList= new HashMap<String, Object>();
            List<HashMap> listOfReservations = new ArrayList<HashMap>();

            for(Reservation reservation1: reservation){
                listOfReservations.add(getSortedReservation(reservation1));
            }

            //using objectmapper and hashmap to customize output
            HashMap<String, Object> reservationList = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();

            String passengerAsJson = mapper.writeValueAsString(passenger);
            passengerAsJson = passengerAsJson.substring(1, passengerAsJson.length()-2);

            reservationList.put("reservation", listOfReservations);
            String reservationAsJson = mapper.writeValueAsString(reservationList);

            String fullOutput = "{\"passenger\":{"+passengerAsJson+",\"reservations\": "+reservationAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity(fullOutput, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity( noPassengerFound(id, "error reading passenger", e.toString()), HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity(noPassengerFound(id, "not found", null), HttpStatus.NOT_FOUND);

            //if passenger found get reservations
            List<Reservation> reservation = reservationRepository.findByPassenger(passenger);

            //get all reservations formatted
            HashMap<String, Object> reservationsList= new HashMap<String, Object>();
            List<HashMap> listOfReservations = new ArrayList<HashMap>();

            for(Reservation reservation1: reservation){
                listOfReservations.add(getSortedReservation(reservation1));
            }

            //using objectmapper and hashmap to customize output
            HashMap<String, Object> reservationList = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();

            String passengerAsJson = mapper.writeValueAsString(passenger);
            passengerAsJson = passengerAsJson.substring(1, passengerAsJson.length()-2);

            reservationList.put("reservation", listOfReservations);
            String reservationAsJson = mapper.writeValueAsString(reservationList);

            String fullOutput = "{\"passenger\":{"+passengerAsJson+",\"reservations\": "+reservationAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity( XML.toString(new JSONObject(fullOutput)), HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(noPassengerFound(null, "error reading passenger", e.toString() ), HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Create Passenger
    */
    @RequestMapping(path="/passenger", method = RequestMethod.POST,
                    produces = {"application/json"})
    public ResponseEntity passenger(@RequestParam(value="firstname") String firstname,
                               @RequestParam(value="lastname") String lastname,
                               @RequestParam(value="age") int age,
                               @RequestParam(value="gender") String gender,
                               @RequestParam(value="phone") Long phone) {

        Passenger passenger = null;
        try {
             passenger = new Passenger(firstname, lastname, age, gender, phone);
             passengerRepository.save(passenger);
             List<Reservation> reservation = reservationRepository.findByPassenger(passenger);

            //using objectmapper to customize output
            ObjectMapper mapper = new ObjectMapper();

            String passengerAsJson = mapper.writeValueAsString(passenger);
            passengerAsJson = passengerAsJson.substring(1, passengerAsJson.length()-2);

            String reservationAsJson = mapper.writeValueAsString(reservation);


            String fullOutput = "{\"passenger\":{"+passengerAsJson+",\"reservations\": {\"reservation\": "+reservationAsJson+"}}}";

            return new ResponseEntity( fullOutput, HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(noPassengerFound(null, "error creating passenger", e.toString() ), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Update Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.PUT,
                    produces = {"application/json"})
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
                return new ResponseEntity(noPassengerFound(id, "not found", null), HttpStatus.NOT_FOUND);

            //if passenger found get reservations
            List<Reservation> reservation = reservationRepository.findByPassenger(pass);

            pass.setFirstname(firstname);
            pass.setLastname(lastname);
            pass.setAge(age);
            pass.setGender(gender);
            pass.setPhone(phone);
            passengerRepository.save(pass);

            //get all reservations formatted
            HashMap<String, Object> reservationsList= new HashMap<String, Object>();
            List<HashMap> listOfReservations = new ArrayList<HashMap>();

            for(Reservation reservation1: reservation){
                listOfReservations.add(getSortedReservation(reservation1));
            }

            //using objectmapper and hashmap to customize output
            HashMap<String, Object> reservationList = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();

            String passengerAsJson = mapper.writeValueAsString(pass);
            passengerAsJson = passengerAsJson.substring(1, passengerAsJson.length()-2);

            reservationList.put("reservation", listOfReservations);
            String reservationAsJson = mapper.writeValueAsString(reservationList);

            String fullOutput = "{\"passenger\":{"+passengerAsJson+",\"reservations\": "+reservationAsJson+"}}";

            System.out.println(fullOutput);

            return new ResponseEntity( fullOutput, HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(noPassengerFound(null, "error updating passenger", e.toString() ), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Delete Passenger
    */
    @RequestMapping(path="/passenger/{id}", method = RequestMethod.DELETE,
                    produces = {"application/xml", "application/json"})
    public ResponseEntity deletePassenger(@PathVariable("id")Long id) {

        Passenger pass = null;
        try {
            pass = passengerRepository.findOne(id);

            //check if passenger exists
            if(pass == null)
                return new ResponseEntity(new JSONObject(noPassengerFound(id, "not found", null)), HttpStatus.NOT_FOUND);

            //if passenger found get reservations
            List<Reservation> reservations = reservationRepository.findByPassenger(pass);

            //cancel all reservations made by this passenger and update the number of seats for the booked flights
            for(Reservation reservation: reservations){

                //get the flight list from each reservation and update the seats and passengers list for each
                for(Flight flight: reservation.getFlights()){
                    flight.removePassenger(pass);
                    flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                }

                reservationRepository.delete(reservation.getOrderNumber());
            }

            passengerRepository.delete(pass);
            

            return new ResponseEntity( XML.toString( new JSONObject( noPassengerFound(id, "passenger deleted", null) ) ), HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(noPassengerFound(null, "error deleting passenger", e.toString() ), HttpStatus.BAD_REQUEST);
        }

    }

}