package com.airlines;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * Created by nehakumar on 4/19/17.
 */

@RestController
public class FlightController {
   @Autowired //to get the bean called FlightRepository
    private FlightRepository flightRepository;


    //Get flight by flighNumber
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.GET)
    public Flight getFlight(@PathVariable("flightNumber")Long number) {
        if(flightRepository.findOne(number) == null)
            //System.out.println("No flight found");
            return null;
        return flightRepository.findOne(number);
    }


    //    Create Flight

    @RequestMapping(path="/flight/{flightNumber}", method = RequestMethod.POST)
    public @ResponseBody Flight flight(@PathVariable("flightNumber") Long flightnumber,
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
    public @ResponseBody Flight updateFlight(@PathVariable("flightNumber") Long flightNumber,
                                       @RequestParam(value="price") int price,
                                       @RequestParam(value="dest_from") String dest_from,
                                       @RequestParam(value="dest_from") String dest_to,
                                       @RequestParam(value="departureTime") Date departureTime,
                                       @RequestParam(value="arrivalTime") Date arrivalTime,
                                       @RequestParam(value="seats_left") int seats_left,
                                       @RequestParam(value="description") String description){

        Flight flight = null;
        try {

            if(flightRepository.findOne(flightNumber) == null)
                System.out.println("No flight found");
            else {
                flight = flightRepository.findOne(flightNumber);
                flight.setArrivalTime(arrivalTime);
                flight.setSeatsLeft(seats_left);
                flight.setDepartureTime(departureTime);
                flight.setDescription(description);
                flight.setFrom(dest_from);
                flight.setTo(dest_to);
                // flight = new Flight(flightNumber,price,dest_from,dest_to,departureTime,arrivalTime, seats_left, description);
                flightRepository.save(flight);
            }
        }catch(Exception e){
            //string msg[]={"msg":"Bad request"};
            return null;//"Unable to find flight"+e.toString();
        }
        return flight;

    }

    //Delete flight by flight number
    @RequestMapping(path="/flight/{flightNumber}",method = RequestMethod.DELETE)
    public String deleteFlight(@PathVariable("flightNumber")Long number) {
        if(flightRepository.findOne(number) == null)
            //System.out.println("No flight found");
            return "No flight found";
        else
            flightRepository.delete(number);
        return "flight deleted successfully";
    }


}
