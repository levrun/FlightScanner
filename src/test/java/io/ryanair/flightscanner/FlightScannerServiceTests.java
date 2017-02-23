package io.ryanair.flightscanner;

import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.dto.FlightScanResult;
import io.ryanair.flightscanner.model.Airport;
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
    private FlightScannerService flightScannerService = new FlightScannerServiceWithNLegs();

    private List<List<Route>> listOfListOf2LegsRoutes;
    private List<List<Route>> listOfListOf3LegsRoutes;

    @Before
    public void setup() {
        Airport wro = new Airport("WRO");
        Airport mad = new Airport("MAD");

        Airport stn = new Airport("STN");
        Airport chq = new Airport("CHQ");

        Route wroMad = new Route(wro, mad);
        Route wroStn = new Route(wro, stn);
        Route stnMad = new Route(stn, mad);
        Route wroChq = new Route(wro, chq);
        Route chqStn = new Route(chq, stn);

        List<Route> listOfDirectRoutes = new ArrayList<>();
        listOfDirectRoutes.add(wroMad);

        List<Route> listOfNonDirect2LegsRoutes = new ArrayList<>();
        listOfNonDirect2LegsRoutes.add(wroStn);
        listOfNonDirect2LegsRoutes.add(stnMad);

        listOfListOf2LegsRoutes = new ArrayList<>();
        listOfListOf2LegsRoutes.add(listOfDirectRoutes);
        listOfListOf2LegsRoutes.add(listOfNonDirect2LegsRoutes);

        List<Route> listOfNonDirect3LegsRoutes = new ArrayList<>();
        listOfNonDirect3LegsRoutes.add(wroChq);
        listOfNonDirect3LegsRoutes.add(chqStn);
        listOfNonDirect3LegsRoutes.add(stnMad);

        listOfListOf3LegsRoutes = new ArrayList<>();
        listOfListOf3LegsRoutes.add(listOfDirectRoutes);
        listOfListOf3LegsRoutes.add(listOfNonDirect2LegsRoutes);
        listOfListOf3LegsRoutes.add(listOfNonDirect3LegsRoutes);

    }

    // @Test
    public void testFlightScanService2LegsMax() {
        // Given
        ReflectionTestUtils.setField(FlightScannerServiceWithTwoLegs.class, "timeForChangePlane", 119);

        Airport wroAirport = new Airport("WRO");
        Airport madAirport = new Airport("MAD");
        Airport stnAirport = new Airport("STN");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTimeFrom = LocalDateTime.parse("2017-03-01 12:30", formatter);
        LocalDateTime dateTimeTo = LocalDateTime.parse("2017-03-10 12:30", formatter);

        Mockito.when(routeService.findAllPossibleRoutesBetweenAirports(wroAirport, madAirport, 2))
                .thenReturn(listOfListOf2LegsRoutes);

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
        LocalDateTime legStnMadCorrectFrom = LocalDateTime.parse("2017-03-02 20:30", formatter);
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

    @Test
    public void testFlightScanService3LegsMax() {
        // Given
        ReflectionTestUtils.setField(FlightScannerServiceWithNLegs.class, "timeForChangePlane", 119);

        Airport wroAirport = new Airport("WRO");
        Airport madAirport = new Airport("MAD");
        Airport stnAirport = new Airport("STN");
        Airport chqAirport = new Airport("CHQ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTimeFrom = LocalDateTime.parse("2017-03-01 12:30", formatter);
        LocalDateTime dateTimeTo = LocalDateTime.parse("2017-03-10 12:30", formatter);

        Mockito.when(routeService.findAllPossibleRoutesBetweenAirports(wroAirport, madAirport, 3))
                .thenReturn(listOfListOf3LegsRoutes);

        List<FlightLeg> wroMadLegs = new ArrayList<>();
        LocalDateTime legWroMadCorrectFrom = LocalDateTime.parse("2017-03-02 12:30", formatter);
        LocalDateTime legWroMadCorrectTo = LocalDateTime.parse("2017-03-02 14:30", formatter);
        FlightLeg legWroMadCorrect = new FlightLeg(wroAirport, madAirport, legWroMadCorrectFrom, legWroMadCorrectTo);
        wroMadLegs.add(legWroMadCorrect);

        Mockito.when(scheduleService.getFlightSchedule(eq(wroAirport), eq(madAirport), any(), any()))
                .thenReturn(wroMadLegs);

        List<FlightLeg> wroChqLegs = new ArrayList<>();
        LocalDateTime legWroChqCorrectFrom = LocalDateTime.parse("2017-03-02 09:00", formatter);
        LocalDateTime legWroChqCorrectTo = LocalDateTime.parse("2017-03-02 10:00", formatter);
        FlightLeg legWroChqCorrect = new FlightLeg(wroAirport, chqAirport, legWroChqCorrectFrom, legWroChqCorrectTo);
        wroChqLegs.add(legWroChqCorrect);

        LocalDateTime legWroChqCorrectFrom2 = LocalDateTime.parse("2017-03-02 10:00", formatter);
        LocalDateTime legWroChqCorrectTo2 = LocalDateTime.parse("2017-03-02 11:00", formatter);
        FlightLeg legWroChqCorrect2 = new FlightLeg(wroAirport, chqAirport, legWroChqCorrectFrom2, legWroChqCorrectTo2);
        wroChqLegs.add(legWroChqCorrect2);

        LocalDateTime legWroChqCorrectFrom3 = LocalDateTime.parse("2017-03-02 10:30", formatter);
        LocalDateTime legWroChqCorrectTo3 = LocalDateTime.parse("2017-03-02 11:00", formatter);
        FlightLeg legWroChqCorrect3 = new FlightLeg(wroAirport, chqAirport, legWroChqCorrectFrom3, legWroChqCorrectTo3);
        wroChqLegs.add(legWroChqCorrect3);

        Mockito.when(scheduleService.getFlightSchedule(eq(wroAirport), eq(chqAirport), any(), any()))
                .thenReturn(wroChqLegs);

        List<FlightLeg> chqStnLegs = new ArrayList<>();
        LocalDateTime legChqStnCorrectFrom = LocalDateTime.parse("2017-03-02 12:00", formatter);
        LocalDateTime legChqStnCorrectTo = LocalDateTime.parse("2017-03-02 13:00", formatter);
        FlightLeg legChqStnCorrect = new FlightLeg(chqAirport, stnAirport, legChqStnCorrectFrom, legChqStnCorrectTo);
        chqStnLegs.add(legChqStnCorrect);

        LocalDateTime legChqStnCorrectFrom2 = LocalDateTime.parse("2017-03-02 13:00", formatter);
        LocalDateTime legChqStnCorrectTo2 = LocalDateTime.parse("2017-03-02 14:00", formatter);
        FlightLeg legChqStnCorrect2 = new FlightLeg(chqAirport, stnAirport, legChqStnCorrectFrom2, legChqStnCorrectTo2);
        chqStnLegs.add(legChqStnCorrect2);

        Mockito.when(scheduleService.getFlightSchedule(eq(chqAirport), eq(stnAirport), any(), any()))
                .thenReturn(chqStnLegs);

        List<FlightLeg> wroStnLegs = new ArrayList<>();
        LocalDateTime legWroStnCorrectFrom = LocalDateTime.parse("2017-03-02 17:30", formatter);
        LocalDateTime legWroStnCorrectTo = LocalDateTime.parse("2017-03-02 18:30", formatter);
        FlightLeg legWroStnCorrect = new FlightLeg(wroAirport, stnAirport, legWroStnCorrectFrom, legWroStnCorrectTo);
        wroStnLegs.add(legWroStnCorrect);

        Mockito.when(scheduleService.getFlightSchedule(eq(wroAirport), eq(stnAirport), any(), any()))
                .thenReturn(wroStnLegs);

        List<FlightLeg> stnMadLegs = new ArrayList<>();
        LocalDateTime legStnMadCorrectFrom = LocalDateTime.parse("2017-03-02 20:30", formatter);
        LocalDateTime legStnMadCorrectTo = LocalDateTime.parse("2017-03-02 21:00", formatter);
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
                3);

        // Then
        Assert.assertThat(results.size(), is(5));


    }

}

