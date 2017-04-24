package com.airlines;

import org.springframework.data.repository.CrudRepository;


public interface ReservationRepository extends CrudRepository<Reservation, Long>{

    Reservation findByPassengerAndFlights(Passenger passenger, Flight flight);
}
