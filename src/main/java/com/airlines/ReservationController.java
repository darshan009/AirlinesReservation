package com.airlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {
    @Autowired

    private ReservationRepository reservationRepository;
    private PassengerRepository passengerRepository;

    /*
        Read reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}")
    public Reservation getReservation(@PathVariable("number")String number) {

        if(reservationRepository.findOne(number) == null)
            System.out.println("No reservations found");
        return reservationRepository.findOne(number);

    }

    /*
        Create reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.POST)
    public Reservation reservation(@PathVariable("number")String number,
                                      @RequestParam(value="passengerId") Long passengerId,
                                      @RequestParam(value="flightLists") List flightLists) {

        Reservation reservation= null;
        try {
            reservation = new Reservation(number, passengerRepository.findOne(passengerId), flightLists);
            reservationRepository.save(reservation);
        }catch (Exception e){
            System.out.println("Error in creating new reservation "+reservation);
            return null;
        }
        return reservation;
    }

    /*
        Create reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.PUT)
    public Reservation updateReservation(@PathVariable("number")String number,
                                   @RequestParam(value="passengerId") Long passengerId,
                                   @RequestParam(value="flightLists") List flightLists) {

        Reservation reservation= null;

        return reservation;
    }
}
