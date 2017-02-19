package io.ryanair.flightscanner.client;

import io.ryanair.flightscanner.model.Route;

import java.util.List;

public interface RoutesClient {

    /**
     * Returns a list of all available routes.
     *
     * @return {@link Route}
     */
    List<Route> getAllDirectRoutes();
}
