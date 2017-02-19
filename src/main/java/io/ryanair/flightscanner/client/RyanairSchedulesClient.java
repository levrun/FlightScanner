package io.ryanair.flightscanner.client;

import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.MonthlySchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.util.Optional;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.SCHEDULES;

@Service
public class RyanairSchedulesClient implements SchedulesClient {

    private static final Logger logger = LoggerFactory.getLogger(RyanairSchedulesClient.class);

    @Value("${ryanair.api.host.url}")
    private String ryanairApiHostUrl;

    @Value("${ryanair.api.schedules.context}")
    private String ryanairApiSchedulesContext;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Cacheable(SCHEDULES)
    public Optional<MonthlySchedule> getSchedules(Airport from, Airport to, YearMonth yearMonth) {
        String schedulesApiUrl = String.format(ryanairApiHostUrl + ryanairApiSchedulesContext,
                from.getName(),
                to.getName(),
                yearMonth.getYear(),
                yearMonth.getMonthValue());
        MonthlySchedule result = null;

        try {
            result = restTemplate.getForObject(schedulesApiUrl, MonthlySchedule.class);
        } catch (HttpClientErrorException exception) {
            // Not all resources of API available, for example if you try to access following URL:
            // https://api.ryanair.com/timetable/3/schedules/TSF/WRO/years/2017/months/3
            // you will get:
            // {"code":"Error","message":"Resource not found"}
            logger.warn("Ryanair end-point responded with exception", exception);
        }

        return Optional.ofNullable(result);
    }
}
