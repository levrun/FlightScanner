package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.dto.FlightLeg;
import io.ryanair.flightscanner.model.Airport;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    /**
     * Finds the list of all available flight legs between two airports for given period.
     *
     * @param from         Airport
     * @param to           Airport
     * @param fromDateTime departure date time
     * @param toDateTime   arrival date time
     * @return list of {@link FlightLeg}
     */
    List<FlightLeg> getFlightSchedule(Airport from,
                                      Airport to,
                                      LocalDateTime fromDateTime,
                                      LocalDateTime toDateTime);

}
