# Ryanair interconnected flights scanner

Spring MVC based RESTful API application which serves information about possible direct
and interconnected flights (maximum 1 stop) based on the data consumed from external APIs.

* Demo is available here:

  `End-point return possible direct and interconnected flights from DUB to TSF in defined period of time`
  https://ryanairflightsearch.herokuapp.com/flightscanner/interconnections?departure=DUB&arrival=TSF&departureDateTime=2017-03-01T07:00&arrivalDateTime=2017-03-03T21:00

* You can also use Swagger-endpoint for convenience:

  https://ryanairflightsearch.herokuapp.com/flightscanner/swagger-ui.html

How to run 
----------

* Java 8 and Maven has to be installed on your machine.
* Just run _Application.java_ and Spring Boot will start embedded tomcat server.
* Open in browser following URL to get info about possible flights between Dublin and Treviso for certain period:

`http://localhost:8080/flightscanner/interconnections?departure=DUB&arrival=TSF&departureDateTime=2017-03-01T07:00&arrivalDateTime=2017-03-03T21:00`

| param             |  value           |
| ----------------- | ----------------:|
| departure         |     DUB          |
| arrival           |     TSF          |
| departureDateTime | 2017-03-01T07:00 |
| arrivalDateTime   | 2017-03-03T21:00 |

Libraries
---------

* spring boot 1.5.1 - dependency injection and web-mvc
* jgrapht 1.0.1 - interconnections between airports
* ehcache 2.10.3 - caching
* swagger 2.4.0 - rest api utilities
* maven - build and war generation

Design
------

```text

                   +--------------------------+        +------------------------+
                   |                          |        |                        |
                   |  RyanairSchedulesClient  |        |  RyanairRoutesClient   |
                   |                          |        |                        |
                   +--------------------------+        +------------------------+
https://api.ryanair.com/timetable/3/schedules             https://api.ryanair.com/core/3/routes/
                                      ^                           ^
                                      |                           |
                     +----------------+------+          +---------+-------------+
                     |                       |          |                       |
                     |    ScheduleService    |          |  JGraphRouteService   |
                     |                       |          |                       |
                     +----------------+------+          +--------+--------------+
                                      ^                          ^
                                      |                          |
                                 +----+--------------------------+----+
                                 |                                    |                                                        /DUB/WRO/years/2016/months/6):
                                 |   FlightScannerServiceWithTwoLegs  |
                                 |                                    |
                                 +-------------------^----------------+
                                                     |
                                     +---------------+-------------+
                                     |                             |
                                     |   FlightScannerController   |
                                     |                             |
                                     +-----------------------------+
    http://localhost:8080/flightscanner/interconnections?departure=DUB&arrival=TSF&departureDateTime=...

```

Requirements:
-------------

1. The application return a list of flights departing from a given departure airport not earlier
  than the specified departure datetime and arriving to a given arrival airport not later than the
  specified arrival datetime.
2. The list should consist of:

* all direct flights if available (for example: `DUB - WRO`)
* all interconnected flights with a maximum of one stop if available (for example: `DUB - STN - WRO`)

3. For interconnected flights the difference between the arrival and the next departure should be 2h or greater
4. The example response should be in following form:

```
[
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "WRO",
                "departureDateTime": "2016-03-01T12:40",
                "arrivalDateTime": "2016-03-01T16:40"
            }
        ]
    },
    {
        "stops": 1,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "STN",
                "departureDateTime": "2016-03-01T06:25",
                "arrivalDateTime": "2016-03-01T07:35"
            },
            {
                "departureAirport": "STN",
                "arrivalAirport": "WRO",
                "departureDateTime": "2016-03-01T09:50",
                "arrivalDateTime": "2016-03-01T13:20"
            }
        ]
    }
]
```