package io.ryanair.flightscanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class MonthlySchedule {

    private final int month;
    private final List<DailySchedule> days;

    @JsonCreator
    public MonthlySchedule(@JsonProperty("month") int month,
                           @JsonProperty("days") List<DailySchedule> days) {
        this.month = month;
        this.days = days;
    }

    public int getMonth() {
        return month;
    }

    public List<DailySchedule> getDays() {
        return days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlySchedule that = (MonthlySchedule) o;
        return month == that.month &&
                Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, days);
    }

    @Override
    public String toString() {
        return "MonthlySchedule{" +
                "month=" + month +
                ", days=" + days +
                '}';
    }
}
