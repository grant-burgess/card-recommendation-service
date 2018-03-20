# Card recommendation API [![Build Status](https://travis-ci.org/grant-burgess/card-recommendation-service.svg?branch=master)](https://travis-ci.org/grant-burgess/card-recommendation-service)

The Card Recommendation REST API integrates with 2 fake credit providers that provide hypothetical clients with recommended credit cards sorted in descending order 

## What was used to ensure quality?
* The project was developed using TDD
* Consumer Driven Contracts via [Pact](https://docs.pact.io/) was used to ensure 3rd party service contracts were adhered to
* [SonarQube](https://www.sonarqube.org/) was used to discover bugs, vulnerabilities and code smells.

### Types of testing used
* Unit tests
* Spring WebMvc test
* [Consumer driven contract](https://martinfowler.com/articles/consumerDrivenContracts.html) integration tests

## Project dependencies
This project uses Java 8 with Spring Boot 2.0.0 and Netflix Hystrix. Tests were written with JUnit4 and [Pact](https://docs.pact.io/) for Consumer Driven Contracts

## Design
The REST API is packaged under `api/v1` to encourage versioning and for maintainers to know where and how to version the API. Credit providers follow a contract with a general request/response and use their own internal models which get transformed into a `CreditProviderResponse`. Credit providers are processed polymorphically, upon any error Hystrix is used with a fallback that simply returns an empty list. In production we would do something more useful, get from cache, retry on specific errors, show a friendly service unavailable message. I use Hystrix to demonstrate how I would go about maintaining a steady state to our API consumers without downtime. Monitoring should be available to know when upstream services are having difficulty.

## To run locally
To run locally simply execute the following from the command line:

```shell
# replace the environment variables `PROVIDER1_ENDPOINT` `PROVIDER2_ENDPOINT` with your desired values

> ./gradlew bootRun \
> -DPROVIDER1_ENDPOINT=http://localhost:8110 \
> -DPROVIDER2_ENDPOINT=http://localhost:8220
```

To run tests
------------

This microservice comes with a number of tests including unit, [WebMvcTest](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-mvc-tests) and SpringBootTest with [Pact](https://docs.pact.io/). Use the following command to execute the tests:

```shell
> ./gradlew test
```

## Deployment

### Prerequisites

Start Docker Machine

```shell
# Start your default Docker Machine
> docker-machine start
```

To package the service in a Docker image, simple execute the following from the command line:

```shell
> ./gradlew dockerBuild
```

### Running

To start up the Card Recommendation Service, simple execute the following from the command line:

```shell
# replace the environment variables `PROVIDER1_ENDPOINT` `PROVIDER2_ENDPOINT` with your desired values
docker run -d -it \
-e PROVIDER1_ENDPOINT=http://localhost:8100 \
-e PROVIDER2_ENDPOINT=http://localhost:8200 \
-p 8080:8080 \
--name card-recommendation-service \
com.grantburgess/card-recommendation-service:0.0.1-SNAPSHOT

# Already created the container?
docker start card-recommendation-service
```

Send a POST request to http://192.168.99.100:8080/v1/creditcards with the body that conforms to:

```JavaScript
{
    "firstname": "Robert C.",
    "lastname": "Martin",
    "dob": "1952-10-04",
    "employment-status": "PART_TIME",
    "salary": 10000,
    "credit-score": 341
}
```