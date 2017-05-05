package com.airlines;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReservationRepository extends CrudRepository<Reservation, Long>{

    List<Reservation> findByPassengerAndFlights(Passenger passenger, Flight flight);

    List<Reservation> findByPassenger(Passenger passenger);

}
