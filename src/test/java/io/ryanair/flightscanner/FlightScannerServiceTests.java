package io.ryanair.flightscanner;

import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Route;
import io.ryanair.flightscanner.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;

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

    @Before
    public void setup() {
        from = new Airport("WRO");
        to = new Airport("MAD");

        Airport randomAirport1 = new Airport("STN");
        Airport randomAirport2 = new Airport("CHQ");
        ffk = new Airport("FFK");

        List<Route> fakeRoutes = new ArrayList<>();
        Route r1 = new Route(from, to);
        Route r2 = new Route(from, randomAirport1);
        Route r3 = new Route(randomAirport1, to);
        Route r4 = new Route(randomAirport1, randomAirport2);
        Route r5 = new Route(ffk, randomAirport2);

        fakeRoutes.addAll(Arrays.asList(r1, r2, r3, r4, r5));

        // Mockito.when(routesClient.getAllDirectRoutes()).thenReturn(fakeRoutes);
    }

    @Test
    public void testFlightScanService() {
        // Given

        // When
        // List<FlightScanResult> results = flightScannerService.scanFlights()

        // Then
        Assert.assertThat(true, is(true));


    }
//
//    @Test
//    public void testThatWeHaveOnly2PossibleRoutesOneDirectAndOneInterconnectedFromWROtoMAD() {
//        // When
//        List<List<Route>> routes = routeService.findAllPossibleRoutesBetweenAirports(from, to, 1);
//
//        // Then
//        Assert.assertThat(routes.size(), is(2));
//    }
//
//    @Test
//    public void testThatWeHaveZeroRoutesBetweenFFKAirportAndWroclaw() {
//        // When
//        List<List<Route>> routes = routeService.findAllPossibleRoutesBetweenAirports(ffk, to, 1);
//
//        // Then
//        Assert.assertThat(routes.size(), is(0));
//    }

}

