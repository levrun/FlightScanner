package io.ryanair.flightscanner.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.utils.DateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, fieldVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({"departureAirport", "arrivalAirport", "departureDateTime", "arrivalDateTime"})
public class FlightLeg {
    private final Airport departureAirport;
    private final Airport arrivalAirport;
    private final LocalDateTime departureDateTime;
    private final LocalDateTime arrivalDateTime;

    @JsonCreator
    public FlightLeg(Airport from,
                     Airport to,
                     LocalDateTime fromDateTime,
                     LocalDateTime toDateTime) {

        this.departureAirport = from;
        this.arrivalAirport = to;
        this.departureDateTime = fromDateTime;
        this.arrivalDateTime = toDateTime;
    }

    @JsonIgnore
    public Airport getDepartureAirport() {
        return departureAirport;
    }

    @JsonGetter("departureAirport")
    public String getDepartureAirportCode() {
        return departureAirport.getName();
    }

    @JsonIgnore
    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    @JsonGetter("arrivalAirport")
    public String getArrivalAirportCode() {
        return arrivalAirport.getName();
    }

    @JsonGetter("departureDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    @JsonGetter("arrivalDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightLeg flightLeg = (FlightLeg) o;
        return Objects.equals(departureAirport, flightLeg.departureAirport) &&
                Objects.equals(arrivalAirport, flightLeg.arrivalAirport) &&
                Objects.equals(departureDateTime, flightLeg.departureDateTime) &&
                Objects.equals(arrivalDateTime, flightLeg.arrivalDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);
    }

    @Override
    public String toString() {
        return "FlightLeg{" +
                "departureAirport=" + departureAirport +
                ", arrivalAirport=" + arrivalAirport +
                ", departureDateTime=" + departureDateTime +
                ", arrivalDateTime=" + arrivalDateTime +
                '}';
    }
}
