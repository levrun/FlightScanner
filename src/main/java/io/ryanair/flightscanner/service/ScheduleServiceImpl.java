package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.client.SchedulesClient;
import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.FLIGHT_SCAN_SEARCH_RESULT;
import static io.ryanair.flightscanner.utils.DateTimeUtils.getYearMonthsForGivenPeriod;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Comparator<FlightLeg> DEPARTURE_DATE_TIME_COMPARATOR = Comparator.comparing(FlightLeg::getDepartureDateTime);

    @Autowired
    private SchedulesClient schedulesClient;

    @Override
    @Cacheable(FLIGHT_SCAN_SEARCH_RESULT)
    public List<FlightLeg> getFlightSchedule(Airport from,
                                             Airport to,
                                             LocalDateTime fromDateTime,
                                             LocalDateTime toDateTime) {

        List<YearMonth> yearMonths = getYearMonthsForGivenPeriod(fromDateTime, toDateTime);
        List<FlightLeg> result = new LinkedList<>();
        yearMonths.forEach(yearMonth -> {
            Optional<MonthlySchedule> schedule = schedulesClient.getSchedules(from, to, yearMonth);
            if (schedule.isPresent()) {
                int month = schedule.get().getMonth();
                int year = yearMonth.getYear();
                List<FlightLeg> monthlySchedule = schedule.get().getDays().stream()
                        .flatMap(day -> {
                            LocalDate date = LocalDate.of(year, month, day.getDay());
                            return day.getFlights().stream()
                                    .map(flight -> createFlightLeg(from, to, date, flight));
                        }).filter(flightSchedule -> isFlightWithinRange(flightSchedule, fromDateTime, toDateTime))
                        .sorted(DEPARTURE_DATE_TIME_COMPARATOR)
                        .collect(Collectors.toList());

                result.addAll(monthlySchedule);
            }
        });
        return result;
    }

    private FlightLeg createFlightLeg(Airport from, Airport to, LocalDate date, Flight flight) {
        LocalDateTime departureDateTime = flight.getDepartureTime().atDate(date);
        LocalDateTime arrivalDateTime = flight.getArrivalTime().atDate(date);
        return new FlightLeg(from, to, departureDateTime, arrivalDateTime);
    }

    private boolean isFlightWithinRange(FlightLeg leg, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        return !leg.getDepartureDateTime().isBefore(fromDateTime) && !leg.getArrivalDateTime().isAfter(toDateTime);
    }

}
