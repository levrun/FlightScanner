package io.ryanair.flightscanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class DailySchedule {

    private final int day;
    private final List<Flight> flights;

    @JsonCreator
    public DailySchedule(@JsonProperty("day") int day,
                         @JsonProperty("flights") List<Flight> flights) {
        this.day = day;
        this.flights = flights;
    }

    public int getDay() {
        return day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailySchedule that = (DailySchedule) o;
        return day == that.day &&
                Objects.equals(flights, that.flights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, flights);
    }

    @Override
    public String toString() {
        return "DailySchedule{" +
                "day=" + day +
                ", flights=" + flights +
                '}';
    }
}
