package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.FLIGHTS_SCAN;

@Service
@Primary
public class FlightScannerServiceWithNLegs implements FlightScannerService {

    private static final int DIRECT_FLIGHT_ONE_LEG = 1;
    private static final int INTERCONNECTED_FLIGHT_TWO_LEGS = 2;
    private static final int NUMBER_OF_INTERCONNECTIONS_FOR_DIRECT_FLIGHT = 0;

    private static final Comparator<FlightLeg> ARRIVAL_DATE_TIME_COMPARATOR = Comparator.comparing(FlightLeg::getArrivalDateTime);

    @Value("${time.for.change.plane.in.minutes}")
    private static int timeForChangePlane;

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

        List<List<Route>> routes = routeService.findAllPossibleRoutesBetweenAirports(from, to, maxConnections);
        List<FlightScanResult> directFlights = searchDirectFlights(routes, fromDateTime, toDateTime);
        List<FlightScanResult> interconnectedFlights = searchInterconnectedFlights(routes, fromDateTime, toDateTime, maxConnections);
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

    private List<FlightScanResult> searchInterconnectedFlights(List<List<Route>> routes,
                                                               LocalDateTime fromDateTime,
                                                               LocalDateTime toDateTime,
                                                               int maxConnections) {
        List<List<Route>> interconnectedRoutes = filterFlightsWithEqualOrMoreThan2Legs(routes);

        List<FlightScanResult> listOfScanResult = new ArrayList<>();

        for(List<Route> routeList: interconnectedRoutes) {
            List<List<FlightLeg>> listOfListOfLegSchedules = new ArrayList<>();
                    LocalDateTime departureDateTimeForNextFlight = fromDateTime;
                    for(Route route : routeList) {
                        List<FlightLeg> legSchedule = scheduleService.getFlightSchedule(route.getFrom(), route.getTo(), departureDateTimeForNextFlight, toDateTime);
                        if(legSchedule.isEmpty()) {
                            // it means we don't have proper schedules on one of the leg
                            // that's why the whole route is impossible
                            listOfListOfLegSchedules = null;
                            break;
                        }
                        listOfListOfLegSchedules.add(legSchedule);
                        departureDateTimeForNextFlight = legSchedule.stream()
                            .findFirst()
                            .map(FlightLeg::getArrivalDateTime)
                            .orElse(fromDateTime)
                            .plusMinutes(timeForChangePlane);
                    }
                    if(listOfListOfLegSchedules != null) {
                        List<FlightScanResult> result = toFlightScanResult(listOfListOfLegSchedules, maxConnections);
                        listOfScanResult.addAll(result);
                    }
        }

        return listOfScanResult;
    }

    private List<Route> filterOnlyDirectFlights(List<List<Route>> routes) {
        return routes.stream().filter(route -> route.size() == DIRECT_FLIGHT_ONE_LEG).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<List<Route>> filterFlightsWithEqualOrMoreThan2Legs(List<List<Route>> connections) {
        return connections.stream().filter(route -> route.size() >= INTERCONNECTED_FLIGHT_TWO_LEGS).collect(Collectors.toList());
    }

    private List<FlightScanResult> toFlightScanResult(List<List<FlightLeg>> listOfListLegSchedule, int maxConnections) {
        List<FlightScanResult> listOfFlightScanResults = new ArrayList<>();
        int depth = 1;
        List<FlightLeg> firstLevelSchedules = listOfListLegSchedule.get(0);
        for(FlightLeg schedule: firstLevelSchedules) {
            List<FlightLeg> listOfOneRoute = new ArrayList<>();
            listOfOneRoute.add(schedule);
            calculatePath(depth, listOfOneRoute, listOfFlightScanResults, listOfListLegSchedule);
        }

        return listOfFlightScanResults;
    }

    private void calculatePath(int depth,
                               List<FlightLeg> listOfOneRoute,
                               List<FlightScanResult> listOfFlightScanResults,
                               List<List<FlightLeg>> listOfListLegSchedule) {

        List<FlightLeg> NLevelSchedules = listOfListLegSchedule.get(depth);

        for(FlightLeg schedule: NLevelSchedules) {
            FlightLeg lastFlightInRoute = listOfOneRoute.get(listOfOneRoute.size() - 1);
            LocalDateTime lastFlightInRouteArrivalDateTime = lastFlightInRoute.getArrivalDateTime();
            LocalDateTime lastFlightInRouteArrivalDateTimePlusTwoHours = lastFlightInRouteArrivalDateTime.plusHours(2);
            LocalDateTime scheduleDepartureDateTime = schedule.getDepartureDateTime();
            boolean scheduleIsOk = scheduleDepartureDateTime.equals(lastFlightInRouteArrivalDateTimePlusTwoHours) || scheduleDepartureDateTime.isAfter(lastFlightInRouteArrivalDateTimePlusTwoHours);

            if(scheduleIsOk) {
                listOfOneRoute.add(schedule);
                if(depth == listOfListLegSchedule.size() - 1) {
                    List<FlightLeg> copyOfOneRoute = new ArrayList<>();
                    copyOfOneRoute.addAll(listOfOneRoute);
                    FlightScanResult result = new FlightScanResult(depth, copyOfOneRoute);
                    listOfFlightScanResults.add(result);
                } else {
                    depth++;
                    calculatePath(depth, listOfOneRoute, listOfFlightScanResults, listOfListLegSchedule);
                    depth--;
                }
                listOfOneRoute.remove(depth);

            }
        }
    }

}

