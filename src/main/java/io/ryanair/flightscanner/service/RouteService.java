package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Route;

import java.util.List;

public interface RouteService {

    List<List<Route>> findAllPossibleRoutesBetweenAirports(Airport from, Airport to, int maxConnections);

}
