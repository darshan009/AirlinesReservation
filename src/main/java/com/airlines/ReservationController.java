package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Transactional
public class ReservationController {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;


    private HashMap noReservationFound(Long number, String msg){
        HashMap<String,Map> hashMap = new HashMap<String,Map>();
        HashMap<String, String> multiValueMap = new HashMap<String, String>();

        String code=null;
        String response=null;

        switch (msg) {
            case "not found":
                msg = "Reservation with number " + number + " does not exist";
                code ="404";
                response ="BadRequest";
                break;
            case "passenger not found":
                msg = "Passenger with id " + number + " does not exist";
                code ="404";
                response ="BadRequest";
                break;
            case "reservation cancelled":
                msg = "Reservation with number " + number + " is cancelled successfully";
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
        Read reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}")
    public ResponseEntity getReservation(@PathVariable("number")Long number) {

        try{

            Reservation reservation = reservationRepository.findOne(number);

            //check if reservation exists or not
            if(reservation == null)
                return new ResponseEntity( noReservationFound(number, "not found"), HttpStatus.NOT_FOUND);

            return new ResponseEntity(reservation, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Create reservation by orderNumber
    */
    @RequestMapping(path="/reservation", method = RequestMethod.POST)
    public ResponseEntity reservation(@RequestParam(value="passengerId") Long passengerId,
                                   @RequestParam(value="flightLists") List<String> flightLists) {

        Reservation reservation= null;

        try {

            List<Flight> flights = new ArrayList<Flight>();

            Passenger passenger = passengerRepository.findOne(passengerId);

            if(passenger != null) {

                //get flights from the above list
                for(String flight: flightLists){
                    flights.add(flightRepository.findOne(flight));
                }

                //check whether each flight has seatsLeft
                for(Flight flight: flights){
                    if(flight.getSeatsLeft() <= 0) {
                        return null;
                    }
                }

                reservation = new Reservation(passenger, flights);
                reservationRepository.save(reservation);

                //add reservation in passenger
                passenger.addReservations(reservation);

                return new ResponseEntity(reservation, HttpStatus.OK);

            }else{
                return new ResponseEntity( noReservationFound(passengerId, "passenger not found"), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Update reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.PUT)
    public ResponseEntity updateReservation(@PathVariable("number")Long number,
                                   @RequestParam(value="flightsAdded") List<String> flightsAdded,
                                    @RequestParam(value="flightsRemoved") List<String> flightsRemoved) {

        Reservation reservation= null;

        try{
            reservation = reservationRepository.findOne(number);

            if(reservation != null) {

                List<Flight> flights =  reservation.getFlights();

                //add flights
                for(String flight: flightsAdded) {
                    flights.add( flightRepository.findOne(flight) );
                }

                //remove flights
                for(String flight: flightsAdded) {
                    flights.remove( flightRepository.findOne(flight) );
                }

                reservation.setFlights(flights);
                reservationRepository.save(reservation);

                return new ResponseEntity(reservation, HttpStatus.OK);

            }else{
                return new ResponseEntity( noReservationFound(number, "not found"), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Cancel reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.DELETE)
    public ResponseEntity deleteReservation(@PathVariable("number")Long number) {

        Reservation reservation= null;
        int seatsToUpdate = 0;
        try{
            reservation = reservationRepository.findOne(number);

            //check if this reservation exists
            if(reservation != null ) {

                List<Flight> flights = reservation.getFlights();

                for (Flight flight : flights) {
                    System.out.println(flight);

                    //update passenger seat remaining
                    seatsToUpdate = flight.getSeatsLeft();
                    flight.setSeatsLeft(seatsToUpdate + 1);
                }

                reservationRepository.delete(reservation);

                return new ResponseEntity( noReservationFound(number, "reservation cancelled"), HttpStatus.NOT_FOUND);

            }else{
                return new ResponseEntity( noReservationFound(number, "not found"), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Search reservation by different params
    */
    @RequestMapping(path="/reservation", method = RequestMethod.GET)
    public ResponseEntity searchReservation(@RequestParam(value="passengerId") Long passengerId,
                                         @RequestParam(value="from") String from,
                                         @RequestParam(value="to") String to,
                                         @RequestParam(value="flightNumber") String flightNumber) {

        Reservation reservation;

        try{

            //search by passenger by id & flights by from, to, flightNumber
            Passenger passenger = passengerRepository.findOne(passengerId);
            Flight flight = flightRepository.findOne(flightNumber);

            reservation = reservationRepository.findByPassengerAndFlights(passenger, flight);

            if(reservation != null) {
                System.out.println("---------------------------------------------");
                System.out.println(reservation);

                return new ResponseEntity(reservation, HttpStatus.OK);
            }else{
                return new ResponseEntity("not found", HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity("not found", HttpStatus.NOT_FOUND);
        }

    }
}
