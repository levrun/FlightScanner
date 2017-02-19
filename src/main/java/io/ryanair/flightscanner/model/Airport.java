package io.ryanair.flightscanner.model;


import java.util.Objects;

/**
 * POJO that represents airport with name as IATA code
 * https://en.wikipedia.org/wiki/International_Air_Transport_Association
 */
public class Airport {

    private final String name;

    public Airport(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(name, airport.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Airport{" +
                "name='" + name + '\'' +
                '}';
    }
}
