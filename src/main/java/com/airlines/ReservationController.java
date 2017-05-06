package com.airlines;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

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
        Error json format display
     */
    private HashMap noReservationFound(Long number, String msg, String err){
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
            case "no seats left":
                msg = "No seats left in the flight";
                code ="404";
                response ="BadRequest";
                break;
            case "reservation cancelled":
                msg = "Reservation with number " + number + " is cancelled successfully";
                code ="200";
                response ="Response";
                break;
            //for error codes
            case "error reading reservation":
                msg = "Error in reading reservation "+err;
                code ="400";
                response ="Response";
                break;
            case "error creating reservation":
                msg = "Error in creating reservation "+err;
                code ="400";
                response ="Response";
                break;
            case "error updating reservation":
                msg = "Error in updating reservation "+err;
                code ="400";
                response ="Response";
                break;
            case "error cancelling reservation":
                msg = "Error in deleting reservation "+err;
                code ="400";
                response ="Response";
                break;
            case "error searching reservation":
                msg = "Error in searching reservation "+err;
                code ="400";
                response ="Response";
                break;
            case "overlapping dates":
                msg = "Error creating reservation, flights have overlapping dates";
                code ="400";
                response ="Response";
                break;
            case "seating capacity exceeded":
                msg = "Error creating reservation, flights seating capacity is exceeded";
                code ="400";
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
        Read reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}",
                    produces = {"application/json"})
    public ResponseEntity getReservation(@PathVariable("number")Long number) {

        try{

            Reservation reservation = reservationRepository.findOne(number);
            System.out.println("in modified method");
            //check if reservation exists or not
            if(reservation == null)
                return new ResponseEntity( noReservationFound(number, "not found", null), HttpStatus.NOT_FOUND);

            //using objectmapper to customize output
            ObjectMapper mapper = new ObjectMapper();

            mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);

            String reservationAsJson = mapper.writeValueAsString(getSortedReservation(reservation));
            reservationAsJson = reservationAsJson.substring(1, reservationAsJson.length()-2);


            String fullOutput = "{\"reservation\":{"+reservationAsJson+"]}}";

            return new ResponseEntity(fullOutput, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity( noReservationFound(null, "error reading reservation", e.toString() ) , HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Create reservation by orderNumber
    */
    @RequestMapping(path="/reservation",
                    method = RequestMethod.POST,
                    produces = {"application/xml"})
    public ResponseEntity reservation(@RequestParam(value="passengerId") Long passengerId,
                                   @RequestParam(value="flightLists") List<String> flightLists) {

        Reservation reservation= null;
        HashMap<Date, Date> dates = new HashMap<Date, Date>();
        Date tempDeparture, tempArrival, tempKey, tempValue;

        try {

            List<Flight> flights = new ArrayList<Flight>();

            Passenger passenger = passengerRepository.findOne(passengerId);

            if(passenger != null) {

                //get flights from the above list
                for(String flight: flightLists){
                    flights.add(flightRepository.findOne(flight));
                    dates.put( (flightRepository.findOne(flight)).getDepartureTime(), (flightRepository.findOne(flight)).getArrivalTime() );
                }

                //check whether each flight has seatsLeft, if yes then subtract one seat for this reservation
                for(Flight flight: flights){

                    //if flight has seats left && does not exceed seating capacity then add the passenger to the flight and subtract a seat
                    if(flight.getSeatsLeft() <= 0) {
                        return new ResponseEntity( noReservationFound( null, "no seats left", null), HttpStatus.BAD_REQUEST);
                    }else if( (flight.getSeatsLeft() + 1) > flight.getFlightCapacity() ) {
                        return new ResponseEntity(noReservationFound(null, "seating capacity exceeded", null), HttpStatus.BAD_REQUEST);
                    }else{
                        flight.addPassenger(passenger);
                        flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                    }

                    //if two flights have overlapping times then break, (StartA <= EndB) and (EndA >= StartB)
                    for (HashMap.Entry<Date, Date> date : dates.entrySet()) {

                        tempDeparture = flight.getDepartureTime();
                        tempArrival = flight.getArrivalTime();
                        tempKey = date.getKey();
                        tempValue = date.getValue();

                        if(tempDeparture != tempKey && tempArrival != tempValue)
                            if(tempDeparture.compareTo(tempKey) <= 0 && tempArrival.compareTo(tempValue) >= 0)
                                return new ResponseEntity( noReservationFound(null, "overlapping dates" ,null), HttpStatus.OK );

                    }


                }

                reservation = new Reservation(passenger, flights);
                reservationRepository.save(reservation);

                //using objectmapper to customize output
                ObjectMapper mapper = new ObjectMapper();

                String reservationAsJson = mapper.writeValueAsString(getSortedReservation(reservation));
                reservationAsJson = reservationAsJson.substring(1, reservationAsJson.length()-2);


                String fullOutput = "{\"reservation\":{"+reservationAsJson+"]}}";

                System.out.println(fullOutput);

                return new ResponseEntity( XML.toString(new JSONObject(fullOutput)), HttpStatus.OK);

            }else{
                return new ResponseEntity( noReservationFound(passengerId, "passenger not found", null), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity(noReservationFound(null, "error creating reservation ", e.toString() ), HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Update reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.POST,
                    produces = {"application/xml"})
    public ResponseEntity updateReservation(@PathVariable("number")Long number,
                                   @RequestParam(value="flightsAdded", required = false) List<String> flightsAdded,
                                    @RequestParam(value="flightsRemoved", required = false) List<String> flightsRemoved) {

        Reservation reservation= null;

        try{
            reservation = reservationRepository.findOne(number);

            if(reservation != null && flightsAdded != null && flightsRemoved != null) {

                //get the original price
                int originalPrice = reservation.getPrice();
                Passenger passenger = reservation.getPassenger();
                List<Flight> flights =  reservation.getFlights();

                //add flights, calculate the price and manage seatsLeft
                for(String flight: flightsAdded) {
                    Flight flightToAdd = flightRepository.findOne(flight);
                    flights.add( flightToAdd );

                    //calculate the price
                    originalPrice += flightToAdd.getPrice();

                    //add the passenger from the flight
                    flightToAdd.addPassenger(passenger);

                    //subtract seats from the newly added flights
                    flightToAdd.setSeatsLeft(flightToAdd.getSeatsLeft() - 1);
                }

                //remove flights, calculate the price and manage seatsLeft
                for(String flight: flightsRemoved) {
                    Flight flightToRemove = flightRepository.findOne(flight);
                    flights.remove( flightToRemove );

                    //calculate the price
                    originalPrice -= flightToRemove.getPrice();

                    //remove passenger
                    flightToRemove.removePassenger(passenger);

                    //add seats to the removed flights
                    flightToRemove.setSeatsLeft(flightToRemove.getSeatsLeft() + 1);
                }

                //update the price
                reservation.setPrice(originalPrice);

                reservation.setFlights(flights);
                reservationRepository.save(reservation);

                //using objectmapper to customize output
                ObjectMapper mapper = new ObjectMapper();

                String reservationAsJson = mapper.writeValueAsString(getSortedReservation(reservation));
                reservationAsJson = reservationAsJson.substring(1, reservationAsJson.length()-2);


                String fullOutput = "{\"reservation\":{"+reservationAsJson+"]}}";

                System.out.println(fullOutput);

                return new ResponseEntity( new JSONObject(fullOutput), HttpStatus.OK);

            }else{
                return new ResponseEntity( noReservationFound(number, "not found", null), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity( noReservationFound(null, "error updating reservation", e.toString() ) , HttpStatus.BAD_REQUEST);
        }

    }

    /*
        Cancel reservation by orderNumber
    */
    @RequestMapping(path="/reservation/{number}", method = RequestMethod.DELETE)
    public ResponseEntity deleteReservation(@PathVariable("number")Long number) {

        Reservation reservation= null;

        try{
            reservation = reservationRepository.findOne(number);

            //check if this reservation exists
            if(reservation != null ) {

                List<Flight> flights = reservation.getFlights();

                for (Flight flight : flights) {

                    //update passenger seat remaining
                    flight.removePassenger(reservation.getPassenger());
                    flight.setSeatsLeft(flight.getSeatsLeft() + 1);

                }

                reservationRepository.delete(reservation);

                return new ResponseEntity( noReservationFound(number, "reservation cancelled", null), HttpStatus.OK);

            }else{
                return new ResponseEntity( noReservationFound(number, "not found", null), HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity( noReservationFound(null, "error cancelling reservation", e.toString() ) , HttpStatus.BAD_REQUEST);
        }
    }

    /*
        Search reservation by different params
    */
    @RequestMapping(path="/reservation", method = RequestMethod.GET,
                    produces = {"application/xml"})
    public ResponseEntity searchReservation(@RequestParam(value="passengerId") Long passengerId,
                                         @RequestParam(value="from") String from,
                                         @RequestParam(value="to") String to,
                                         @RequestParam(value="flightNumber") String flightNumber) {

        try{

            //search by passenger by id & flights by from, to, flightNumber
            Passenger passenger = passengerRepository.findOne(passengerId);
            Flight flight = flightRepository.findOne(flightNumber);


            List<Reservation> reservations = reservationRepository.findByPassengerAndFlights(passenger, flight);

            //get sorted reseravtion list
            List<HashMap> reservationSorted = new ArrayList<HashMap>();

            if(reservations != null && flight != null && passenger != null) {

                //using objectmapper to customize output
                ObjectMapper mapper = new ObjectMapper();

                //loop through all reservations to get sorted list of reservations
                for(Reservation reservation: reservations){
                    reservationSorted.add( getSortedReservation(reservation) );
                }
                String reservationAsJson = mapper.writeValueAsString(reservationSorted);

                String fullOutput = "{\"reservations\": {\"reservation\":"+reservationAsJson+"}}}";

                System.out.println(fullOutput);

                return new ResponseEntity( XML.toString(new JSONObject(fullOutput)), HttpStatus.OK);


            }else{
                return new ResponseEntity( noReservationFound(null, "not found", null) , HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity( noReservationFound(null, "error searching reservation", e.toString() ) , HttpStatus.BAD_REQUEST);
        }

    }
}
