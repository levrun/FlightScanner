package io.ryanair.flightscanner.client;

import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.MonthlySchedule;

import java.time.YearMonth;
import java.util.Optional;

public interface SchedulesClient {

    /**
     * Schedule API
     * Returns a list of available flights for a given from airport,
     * an to airport, a year and a month
     *
     * @param from      departure airport
     * @param to        arrival airport
     * @param yearMonth year and month
     * @return optional value of {@link MonthlySchedule}
     */
    Optional<MonthlySchedule> getSchedules(Airport from, Airport to, YearMonth yearMonth);

}
