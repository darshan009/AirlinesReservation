package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class ReservationController {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;


    /*
        Read reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}")
    public Reservation getReservation(@PathVariable("number")Long number) {

        Reservation reservation = reservationRepository.findOne(number);
        System.out.println(reservation);
        if(reservation == null) {
            System.out.println("No reservations found");
            return null;
        }
        return reservation;

    }

    /*
        Create reservation by orderNumber
    */
    @RequestMapping(path="/reservation", method = RequestMethod.POST)
    public Reservation reservation(@RequestParam(value="passengerId") Long passengerId,
                                   @RequestParam(value="flightLists") List flightLists) {

        Reservation reservation= null;
        try {
            int price = 120;

            Passenger passenger = passengerRepository.findOne(passengerId);
            if(passenger != null) {

                reservation = new Reservation(passenger, price, flightLists);
                System.out.println(reservation);
                reservationRepository.save(reservation);

            }else{
                System.out.println("No passenger found");
                return null;
            }

        }catch (Exception e){
            System.out.println("Error in creating new reservation "+e);
            return null;
        }
        return reservation;
    }

    /*
        Update reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.PUT)
    public Reservation updateReservation(@PathVariable("number")Long number,
                                   @RequestParam(value="flightsAdded") List flightsAdded,
                                    @RequestParam(value="flightsRemoved") List flightsRemoved) {

        Reservation reservation= null;
        List<Flight> flightsToAdd = flightsAdded;
        List<Flight> flightsToRemove = flightsRemoved;

        try{
            reservation = reservationRepository.findOne(number);
            List flights =  reservation.getFlights();

            //add flights
            for(Flight flight: flightsToAdd) {
                flights.add(flight);
            }

            //remove flights
            for(Flight flight: flightsToRemove) {
                flights.remove(flight);
            }

            reservation.setFlights(flights);
            reservationRepository.save(reservation);

        }catch (Exception e){
            System.out.println("Error in updating reservation "+e);
            return null;
        }

        return reservation;
    }

    /*
        Cancel reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.DELETE)
    public String deleteReservation(@PathVariable("number")Long number) {

        Reservation reservation= null;
        int seatsToUpdate = 0;
        try{
            reservation = reservationRepository.findOne(number);
            List<Flight> flights = reservation.getFlights();

            for(Flight flight: flights) {
                System.out.println(flight);

                //update passenger seat remaining
                seatsToUpdate = flight.getSeatsLeft();
                flight.setSeatsLeft(seatsToUpdate + 1);
            }

            reservationRepository.delete(reservation);
        }catch (Exception e){
            System.out.println("Error in deleting reservation "+e);
            return null;
        }

        return "Successfully deleted reservation";
    }

    /*
        Search reservation by orderNumber
    */
    @RequestMapping(path="/reservation", method = RequestMethod.DELETE)
    public Reservation searchReservation(@RequestParam(value="passengerId") Long passengerId,
                                         @RequestParam(value="from") String from,
                                         @RequestParam(value="to") String to,
                                         @RequestParam(value="flightNumber") Long flightNumber) {

        Reservation reservation;

        try{

            //search by passenger by id & flights by from, to, flightNumber
            Passenger passenger = passengerRepository.findOne(passengerId);
            Flight flight = flightRepository.findOne(flightNumber);

            reservation = reservationRepository.findByPassengerAndFlights(passenger, flight);

            System.out.println("---------------------------------------------");
            System.out.println(reservation);

        }catch (Exception e){
            System.out.println("Error in deleting reservation "+e);
            return null;
        }

        return null;
    }
}
