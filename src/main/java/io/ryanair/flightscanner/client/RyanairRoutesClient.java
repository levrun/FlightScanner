package io.ryanair.flightscanner.client;

import io.ryanair.flightscanner.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.ALL_ROUTES;

@Service
public class RyanairRoutesClient implements RoutesClient {

    @Value("${ryanair.api.host.url}")
    private String ryanairApiHostUrl;

    @Value("${ryanair.api.routes.context}")
    private String ryanairApiRoutesContext;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Cacheable(ALL_ROUTES)
    public List<Route> getAllDirectRoutes() {
        String routesApiUrl = ryanairApiHostUrl + ryanairApiRoutesContext;
        ResponseEntity<Route[]> responseEntity = restTemplate.getForEntity(routesApiUrl, Route[].class);
        return Arrays.asList(responseEntity.getBody());
    }
}
