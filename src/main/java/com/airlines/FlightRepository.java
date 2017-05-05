package com.airlines;

import org.springframework.data.repository.CrudRepository;


public interface FlightRepository extends CrudRepository<Flight, String> {
}
