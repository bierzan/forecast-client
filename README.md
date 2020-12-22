# Five Day Forecast App
Service to get five-day weather forecast for every main polish administrative area (voivodeship).

Workshop application to practice reactive approach to REST service.

## Table of contents
* [Technologies](#technologies)
* [How to run](#how-to-run)
* [Features](#features)
* [Sidenotes](#sidenotes)
	
## Technologies
**Project created with:**

* Java 1.8
* Spring Boot 2.4.0

**Tests framework and tools**:

* Spock 1.3
* MockWebServer 3.14
* WireMock 2.27
	
## How to run
To run this project type command:

* If maven is installed: 
    * ```mvn spring-boot:run```

* If not, use maven wrapper: 
    * linux: ```/mvnw spring-boot:run```
    * windows: ```/mvnw.cmd spring-boot:run```

## Features
Application provides REST Api in terms of:

### Providing weather forecast

By using polish postal code as a location reference, service matches it with a voivodeship and provides weather forecast for next five days. Weather data is gathered from Accuweather.

* endpoint: ```/forecasts/voivodeships/5day/{postalCode}```
* response body model:
```json
{
  "dailyForecast": [
    {
      "date": "2020-11-26",
      "temperature": {
        "max": 0,
        "min": 0,
        "unit": "C"
      }
    },
    {
      "date": "2020-11-27",
      "temperature": {
        "max": 0,
        "min": 0,
        "unit": "C"
      }
    }
  ]
}
```

As daily Accuweather forecasts are refreshed every 4 hours, application stores data from previous requests. 
Queried postal codes are bonded to voivodeships, 
and when forecast response from Accuweather is received it's stored together with related voivodeships.

Application runs scheduled weather forecasts updates, so it minimizes response time and number of calls to third party service.
Also after each update service saves repository snapshot file. After reboot, it uses this file to restore already collected data. 

### Providing number of requests send to Accuweather

Application tracks number of requests to Accuweather. 

* requests are divided to:
    * location request - to get weather forecast from Accuweather you need to obtain location key first. 
    Location key can be found by city name, geographic coordinates or postal code.
    * forecast request - calls for weather forecast for previously received location key

Rebooting application resets counter.

* endpoint: ```/statistics/requests/accuweather```
* response body model:
```json
{
  "locationCalls": 0,
  "weatherCalls": 0,
  "total": 0
}
```
## Sidenotes
* Application provides swagger-ui for endpoint representation. 
By default, when running locally go to this url to access swagger: 
```localhost:8080/swagger-ui/```
* By default, application starts empty repository. However, You can find snapshot file [HERE](src/main/resources/repo/XXrepo-snapshot.json).
To put this into repository, just change file name to correspond with ```forecast.repository.snapshot.path``` parameter in
 [forecast.properties](src/main/resources/forecast.properties) file and start application
* First call for weather forecast is always very slow. 
The reason for this is that Spring WebClient uses Netty for non-blocking requests. 
Unfortunately, it comes with this minor drawback which is caused by lazy initialization of Netty context.
For explanation go [HERE](https://github.com/spring-projects/spring-framework/issues/21734).
This problem occurs only by first call. Consecutive requests work without any delay.

