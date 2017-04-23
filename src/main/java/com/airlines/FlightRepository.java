package com.airlines;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by nehakumar on 4/19/17.
 */

public interface FlightRepository extends CrudRepository<Flight, Long> {
}
