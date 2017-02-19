package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.FLIGHTS_SCAN;

@Service
public class FlightScannerServiceWithTwoLegs implements FlightScannerService {

    private static final int DIRECT_FLIGHT_ONE_LEG = 1;
    private static final int INTERCONNECTED_FLIGHT_TWO_LEGS = 2;
    private static final int NUMBER_OF_INTERCONNECTIONS_FOR_DIRECT_FLIGHT = 0;

    private static final Comparator<FlightLeg> ARRIVAL_DATE_TIME_COMPARATOR = Comparator.comparing(FlightLeg::getArrivalDateTime);

    @Value("${time.for.change.plane.in.minutes}")
    private int timeForChangePlane;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    @Cacheable(FLIGHTS_SCAN)
    public List<FlightScanResult> scanFlights(Airport from,
                                              Airport to,
                                              LocalDateTime fromDateTime,
                                              LocalDateTime toDateTime,
                                              int maxConnections) {

        List<List<Route>> routes = routeService.findAllPossibleRoutesBetweenAirports(from, to, INTERCONNECTED_FLIGHT_TWO_LEGS);
        List<FlightScanResult> directFlights = searchDirectFlights(routes, fromDateTime, toDateTime);
        List<FlightScanResult> interconnectedFlights = searchInterconnectedFlights(routes, fromDateTime, toDateTime);
        List<FlightScanResult> result = new ArrayList<>();

        result.addAll(directFlights);
        result.addAll(interconnectedFlights);

        return result;
    }

    private List<FlightScanResult> searchDirectFlights(List<List<Route>> routes,
                                                       LocalDateTime fromDateTime,
                                                       LocalDateTime toDateTime) {

        List<Route> directRoutes = filterOnlyDirectFlights(routes);

        List<FlightLeg> directFlights = directRoutes.stream().map(route ->
                scheduleService.getFlightSchedule(route.getFrom(), route.getTo(), fromDateTime, toDateTime))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return directFlights.stream()
                .map(flightSchedule -> new FlightScanResult(NUMBER_OF_INTERCONNECTIONS_FOR_DIRECT_FLIGHT, Collections.singletonList(flightSchedule)))
                .collect(Collectors.toList());
    }

    private List<FlightScanResult> searchInterconnectedFlights(List<List<Route>> routes, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<List<Route>> interconnectedRoutes = filterFlightsWith2Legs(routes);
        return interconnectedRoutes.parallelStream()
                .flatMap(interconnectedRoute -> {
                    Route firstLeg = interconnectedRoute.get(0);
                    Route secondLeg = interconnectedRoute.get(1);

                    List<FlightLeg> firstLegSchedule = scheduleService.getFlightSchedule(firstLeg.getFrom(), firstLeg.getTo(), fromDateTime, toDateTime);

                    LocalDateTime departureDateTimeForNextFlight = firstLegSchedule.stream()
                            .findFirst()
                            .map(FlightLeg::getArrivalDateTime)
                            .orElse(fromDateTime)
                            .plusMinutes(timeForChangePlane);

                    List<FlightLeg> secondLegSchedule = scheduleService.getFlightSchedule(secondLeg.getFrom(), secondLeg.getTo(), departureDateTimeForNextFlight, toDateTime);
                    return toFlightSearchData(firstLegSchedule, secondLegSchedule);
                }).collect(Collectors.toList());
    }

    private List<Route> filterOnlyDirectFlights(List<List<Route>> routes) {
        return routes.stream().filter(route -> route.size() == DIRECT_FLIGHT_ONE_LEG).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<List<Route>> filterFlightsWith2Legs(List<List<Route>> connections) {
        return connections.stream().filter(route -> route.size() == INTERCONNECTED_FLIGHT_TWO_LEGS).collect(Collectors.toList());
    }

    private Stream<FlightScanResult> toFlightSearchData(List<FlightLeg> firstLegSchedule, List<FlightLeg> secondLegSchedule) {
        firstLegSchedule.sort(ARRIVAL_DATE_TIME_COMPARATOR);
        secondLegSchedule.sort(ARRIVAL_DATE_TIME_COMPARATOR);

        return firstLegSchedule.stream()
                .flatMap(firstLeg -> secondLegSchedule.stream()
                        .filter(secondLeg -> secondLeg.getDepartureDateTime().isAfter(firstLeg.getArrivalDateTime().plusMinutes(timeForChangePlane)))
                        .map(secondLeg -> new FlightScanResult(1, Arrays.asList(
                                new FlightLeg(firstLeg.getDepartureAirport(), firstLeg.getArrivalAirport(), firstLeg.getDepartureDateTime(), firstLeg.getArrivalDateTime()),
                                new FlightLeg(secondLeg.getDepartureAirport(), secondLeg.getArrivalAirport(), secondLeg.getDepartureDateTime(), secondLeg.getArrivalDateTime())
                        )))
                );
    }

}
