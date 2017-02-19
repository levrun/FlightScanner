package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightScannerService {

    /**
     * Scan all flights with maxConnections between two airports for given period
     *
     * @param from         airport
     * @param to           airport
     * @param fromDateTime start date time
     * @param toDateTime   finish date time
     * @return list of {@link FlightScanResult}
     */
    List<FlightScanResult> scanFlights(Airport from,
                                       Airport to,
                                       LocalDateTime fromDateTime,
                                       LocalDateTime toDateTime,
                                       int maxConnections);

}
