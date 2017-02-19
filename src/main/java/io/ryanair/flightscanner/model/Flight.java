package io.ryanair.flightscanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.Objects;

public class Flight {

    private final String number;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;

    @JsonCreator
    public Flight(@JsonProperty("number") String number,
                  @JsonProperty("departureTime") String departureTime,
                  @JsonProperty("arrivalTime") String arrivalTime) {

        this.number = number;
        this.departureTime = LocalTime.parse(departureTime);
        this.arrivalTime = LocalTime.parse(arrivalTime);
    }

    public String getNumber() {
        return number;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(number, flight.number) &&
                Objects.equals(departureTime, flight.departureTime) &&
                Objects.equals(arrivalTime, flight.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, departureTime, arrivalTime);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "number='" + number + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
