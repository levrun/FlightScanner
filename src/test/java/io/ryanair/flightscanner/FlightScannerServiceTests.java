package io.ryanair.flightscanner;

import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Flight;
import io.ryanair.flightscanner.model.Route;
import io.ryanair.flightscanner.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class FlightScannerServiceTests {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private RouteService routeService;

    @InjectMocks
    private FlightScannerService flightScannerService = new FlightScannerServiceWithTwoLegs();

    private Airport from;
    private Airport to;
    private Airport ffk;
    private List<List<Route>> listOfListOfRoutes;

    @Before
    public void setup() {
        from = new Airport("WRO");
        to = new Airport("MAD");

        Airport randomAirport1 = new Airport("STN");
        Airport randomAirport2 = new Airport("CHQ");
        ffk = new Airport("FFK");

        Route r1 = new Route(from, to);
        Route r2 = new Route(from, randomAirport1);
        Route r3 = new Route(randomAirport1, to);
        Route r4 = new Route(randomAirport1, randomAirport2);
        Route r5 = new Route(ffk, randomAirport2);

        List<Route> listOfDirectRoutes = new ArrayList<>();
        listOfDirectRoutes.add(r1);

        List<Route> listOfNonDirectRoutes = new ArrayList<>();
        listOfNonDirectRoutes.add(r2);
        listOfNonDirectRoutes.add(r3);

        listOfListOfRoutes = new ArrayList<>();
        listOfListOfRoutes.add(listOfDirectRoutes);
        listOfListOfRoutes.add(listOfNonDirectRoutes);

    }

    @Test
    public void testFlightScanService() {
        // Given
        ReflectionTestUtils.setField(FlightScannerServiceWithTwoLegs.class, "timeForChangePlane", 119);

        Airport wroAirport = new Airport("WRO");
        Airport madAirport = new Airport("MAD");
        Airport stnAirport = new Airport("STN");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTimeFrom = LocalDateTime.parse("2017-03-01 12:30", formatter);
        LocalDateTime dateTimeTo = LocalDateTime.parse("2017-03-10 12:30", formatter);

        Mockito.when(routeService.findAllPossibleRoutesBetweenAirports(wroAirport, madAirport, 2))
                .thenReturn(listOfListOfRoutes);

        List<FlightLeg> wroMadLegs = new ArrayList<>();
        LocalDateTime legWroMadCorrectFrom = LocalDateTime.parse("2017-03-02 12:30", formatter);
        LocalDateTime legWroMadCorrectTo = LocalDateTime.parse("2017-03-02 14:30", formatter);
        FlightLeg legWroMadCorrect = new FlightLeg(wroAirport, madAirport, legWroMadCorrectFrom, legWroMadCorrectTo);
        wroMadLegs.add(legWroMadCorrect);

        Mockito.when(scheduleService.getFlightSchedule(eq(wroAirport), eq(madAirport), any(), any()))
                .thenReturn(wroMadLegs);

        List<FlightLeg> wroStnLegs = new ArrayList<>();
        LocalDateTime legWroStnCorrectFrom = LocalDateTime.parse("2017-03-02 17:30", formatter);
        LocalDateTime legWroStnCorrectTo = LocalDateTime.parse("2017-03-02 18:30", formatter);
        FlightLeg legWroStnCorrect = new FlightLeg(wroAirport, stnAirport, legWroStnCorrectFrom, legWroStnCorrectTo);
        wroStnLegs.add(legWroStnCorrect);

        Mockito.when(scheduleService.getFlightSchedule(eq(wroAirport), eq(stnAirport), any(), any()))
                .thenReturn(wroStnLegs);

        List<FlightLeg> stnMadLegs = new ArrayList<>();
        LocalDateTime legStnMadCorrectFrom = LocalDateTime.parse("2017-03-02 20:31", formatter);
        LocalDateTime legStnMadCorrectTo = LocalDateTime.parse("2017-03-02 21:30", formatter);
        FlightLeg legStnMadCorrect = new FlightLeg(stnAirport, madAirport, legStnMadCorrectFrom, legStnMadCorrectTo);
        stnMadLegs.add(legStnMadCorrect);

        Mockito.when(scheduleService.getFlightSchedule(eq(stnAirport), eq(madAirport), any(), any()))
                .thenReturn(stnMadLegs);

        // When
        List<FlightScanResult> results = flightScannerService.scanFlights(
                wroAirport,
                madAirport,
                dateTimeFrom,
                dateTimeTo,
                2);

        // Then
        Assert.assertThat(results.size(), is(2));


    }

}

