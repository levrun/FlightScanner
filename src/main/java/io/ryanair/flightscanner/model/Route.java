package io.ryanair.flightscanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Route {

    private final Airport from;
    private final Airport to;

    @JsonCreator
    public Route(@JsonProperty("airportFrom") Airport from,
                 @JsonProperty("airportTo") Airport to) {
        this.from = from;
        this.to = to;
    }

    public Airport getFrom() {
        return from;
    }

    public Airport getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(from, route.from) &&
                Objects.equals(to, route.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Route{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
