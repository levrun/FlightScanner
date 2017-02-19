package io.ryanair.flightscanner.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class FlightScanResult {

    private final int stops;
    private final List<FlightLeg> legs;

    @JsonCreator
    public FlightScanResult(@JsonProperty("stops") int stops,
                            @JsonProperty("legs") List<FlightLeg> legs) {
        this.stops = stops;
        this.legs = legs;
    }

    public int getStops() {
        return stops;
    }

    public List<FlightLeg> getLegs() {
        return legs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightScanResult that = (FlightScanResult) o;
        return stops == that.stops &&
                Objects.equals(legs, that.legs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stops, legs);
    }

    @Override
    public String toString() {
        return "FlightScanResult{" +
                "stops=" + stops +
                ", legs=" + legs +
                '}';
    }
}
