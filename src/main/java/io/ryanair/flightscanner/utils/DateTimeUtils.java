package io.ryanair.flightscanner.utils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static List<YearMonth> getYearMonthsForGivenPeriod(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        YearMonth from = YearMonth.from(fromDateTime);
        YearMonth to = YearMonth.from(toDateTime);
        List<YearMonth> months = new ArrayList<>();
        months.add(from);
        if (to.isAfter(from)) {
            YearMonth month = from;
            do {
                month = month.plusMonths(1);
                months.add(month);
            }
            while (month.isBefore(to));
        }
        return months;
    }
}
