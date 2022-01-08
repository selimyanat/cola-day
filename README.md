# Cola day Application

## Introduction

The cola day application is a CRUD application that meets the following business problem: 

> Two companies, COKE and PEPSI, are sharing an office building. Tomorrow is COLA day (for one day), that the two companies are celebrating.  They are hosting a number of business partners in the building.
> 
>  In order to optimize space utilization, they have decided to set-up a joint booking system where any user can book one of the 20 meeting rooms available, 10 from each company (C01, C02, ... , C10 and P01, P02, .... , P10).
>   
>  The two companies would prefer that they do not  have to use a central booking system for this (as they do not trust each other or anyone else to not take advantage of the situation) - but it’s up to you to decide if you want to implement this functionality or not, and how. 
>  
>  The booking system has the following functionalities: 
>  
> ● Each company has its own room booking front-end  application (note: you may develop only one of them)
>  
>  ● Users can see hourly meeting room availabilities of any of the 20 meeting rooms on COLA day (8am-9am, 9am-10am, etc.)
>  
> ● Users can book meeting rooms by  the hour (first come first served)
> 
> ● Users can cancel their own reservations


## Requirements

 - Java 11 or higher
 - Docker
 - A version of Kubernetes running locally: Docker for Desktop (Mac or Windows) with Kubernetes support; or MiniKube or K3s
 - Kubernetes client
 - Helm 3 or higher

## Build
  
```  
make build
```

## Run the application

###  IDE

Run the main class `Bootstrap`

###  Maven

Open your terminal, navigate to the `cola-day` source directory then run the following command 
```
make run-in-memory
```

### Kubernetes

Navigate to the `cola-day` source directory then:

- Build the application by running the following command:
```
make build
```

- Create a kubernetes namespace by running the following command:
```
make create-kubernetes-namespace
```
- Deploy (or upgrade) the application to kubernetes by running the following command:
```
make deploy-to-kubernetes
```
- Expose the application outside kubernetes by using a port-forward with the following command:
```
export POD_NAME=$(kubectl get pods --namespace coladay -l "app.kubernetes.io/name=coladay-chart,app.kubernetes.io/instance=coladay" -o jsonpath="{.items[0].metadata.name}")
export CONTAINER_PORT=$(kubectl get pod --namespace coladay $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
kubectl --namespace coladay port-forward $POD_NAME 8080:$CONTAINER_PORT 
```

## Design

Coladay is a self-contained CRUD application based on **Spring Boot** that runs an embedded servlet 
container running by default on port 8080 that expose a **REST API**. The following is a list of the
most important libraries used to develop this application:

 - [Spring boot](https://spring.io/projects/spring-boot): Simple and rapid framework to create simple and web based applications.
 - [Spring data rest](https://projects.spring.io/spring-data-rest/): Spring library that analyzes the entity repositories and expose them as REST resources.
 - [Spring HATEOAS](https://spring.io/projects/spring-hateoas):  Spring library that allows to create REST representation that stick with the principle of HATEOAS *([Hypertext as the Engine of Application State](https://www.wikiwand.com/en/HATEOAS)*)
 - [Spring restdocs](https://docs.spring.io/spring-restdocs/docs/2.0.0.RELEASE/reference/html5/#introduction): Spring library that allows to create api documentation out of tests.
 - [H2](http://www.h2database.com/html/main.html): a lightweight relational database.
 - [lombok](https://projectlombok.org/) : Framework auto generating code for java (getter, setter, ...).
 - [vavr](http://www.vavr.io): Functional library for java.
 - [Junit 5](https://junit.org/junit5/): The next generation of testing framework for java.
 - [AssertionsJ](http://joel-costigliola.github.io/assertj/): Fluent assertions for java.

## API

Visit the API documentation [here](https://selimyanat.github.io/cola-day/) !