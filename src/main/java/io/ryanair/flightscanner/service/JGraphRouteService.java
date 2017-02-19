package io.ryanair.flightscanner.service;

import io.ryanair.flightscanner.client.RoutesClient;
import io.ryanair.flightscanner.model.Airport;
import io.ryanair.flightscanner.model.Route;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.ryanair.flightscanner.common.CacheConfigurationConstants.SPECIFIC_ROUTES;

@Service
public class JGraphRouteService implements RouteService {

    @Autowired
    private RoutesClient routesClient;

    @Override
    @Cacheable(SPECIFIC_ROUTES)
    public List<List<Route>> findAllPossibleRoutesBetweenAirports(Airport from, Airport to, int maxConnections) {
        List<Route> allDirectedRoutes = routesClient.getAllDirectRoutes();
        DirectedGraph<Airport, DefaultEdge> graph = getAvailableRoutesGraph(allDirectedRoutes);

        AllDirectedPaths<Airport, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
        List<GraphPath<Airport, DefaultEdge>> listWithAllAvailablePatches = directedPaths.getAllPaths(from, to, true, maxConnections + 1);

        List<List<Route>> result;
        result = listWithAllAvailablePatches.stream().map(path -> {
            List<Airport> airports = path.getVertexList();
            List<Route> routes = new ArrayList<>();
            for (int i = 0; i < airports.size() - 1; i++) {
                Route route = new Route(airports.get(i), airports.get(i + 1));
                routes.add(route);
            }
            return routes;
        }).collect(Collectors.toList());
        return result;
    }

    private DirectedGraph<Airport, DefaultEdge> getAvailableRoutesGraph(List<Route> list) {
        DirectedGraph<Airport, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        list.forEach(route -> {
            graph.addVertex(route.getFrom());
            graph.addVertex(route.getTo());
            graph.addEdge(route.getFrom(), route.getTo());
        });

        return graph;
    }

}
