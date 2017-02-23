package io.ryanair.flightscanner.controller;

import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.service.FlightScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FlightScannerController {

//    @Value("${max.connections}")
//    private int maxConnections;

    @Autowired
    private FlightScannerService flightScannerService;

    @RequestMapping(value = "/interconnections", produces = "application/json")
    public List<FlightScanResult> scanFlights(@RequestParam String departure,
                                              @RequestParam String arrival,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime,
                                              @RequestParam Integer maxConnections) {

        Airport from = new Airport(departure);
        Airport to = new Airport(arrival);
        List<FlightScanResult> results;
        results = flightScannerService.scanFlights(from,
                to,
                departureDateTime,
                arrivalDateTime,
                maxConnections);
        return results;
    }

}
