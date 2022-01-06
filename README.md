# Cola day Application

## Introduction

The cola day application is a central web based application that meets the following business problem: 

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

The following section describes the key decisions and assumptions made to develop the application 
along with an overview of the technical environment.

### Assumptions

The application has been implemented around the following assumptions:

 - A room can be booked for one hour.
 - The available time slots to make a reservation are the following:
	 - 8:00 am - 09:00 am
	 - 09:00 am - 10:00 am
	 - 10:00 am - 11:00 am
	 - 11:00 am - 12:00 pm
	 - 12:00 pm - 13:00 pm
	 - 13:00 pm - 14:00 pm
	 - 14:00 pm - 15:00 pm
	 - 15:00 pm - 16:00 pm
	 - 16:00 pm - 17:00 pm
	 - 17:00 pm - 18:00 pm
 - There are 20 rooms.
 - According to the available time slots and the number of rooms there can be 200 reservations at most.
 - The rooms are pretty similar in term of size.
 - In order to strike fairness in terms of reservation numbers, both companies will be a given a configured quota  number that default  to 100 reservations. 
 -  Both companies, to ease the reservations process, had put in charge `user1` and `user2` resp for `COKE` and `PEPSI` . However the system is open to put more users.

### Technical Environment

Coladay is a self contained application based on **Spring Boot** that runs an embedded servlet container running by default on port 8080 that expose a **REST API**.  

 - [Spring boot](https://spring.io/projects/spring-boot): Simple and rapid framework to create simple and web based applications.
 - [Spring data rest](https://projects.spring.io/spring-data-rest/): Spring library that analyzes the entity repositories and expose them as REST resources.
 - [Spring HATEOAS](https://spring.io/projects/spring-hateoas):  Spring library that allows to create REST representation that stick with the principle of HATEOAS *([Hypertext as the Engine of Application State](https://www.wikiwand.com/en/HATEOAS)*)
 - [H2](http://www.h2database.com/html/main.html): a lightweight relational database.
 - [lombok](https://projectlombok.org/) : Framework auto generating code for java (getter, setter, ...).
 - [vavr](http://www.vavr.io): Functional library for java.
 - [Junit 5](https://junit.org/junit5/): The next generation of testing framework for java.
 - [AssertionsJ](http://joel-costigliola.github.io/assertj/): Fluent assertions for java.

### Configuration

The following lists the available configuration keys:

```
# Quota configuration  
coke.quota= 100  
pepsi.quota= 100  

# Database settings 
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;Mode=MYSQL  
spring.datasource.username=sa  
spring.datasource.password=  
spring.datasource.driver-class-name=org.h2.Driver  
# Let hibernate generate the DDL schema  
spring.jpa.hibernate.ddl-auto=create-drop  

  # Log configuration  
logging.level.com.coladay=DEBUG
```

## Features

This section list the main features provided by the application through a REST API.  Note you can   
find more by navigating the links relations in the responses

### Create a reservation by specifying a room and a time slot

```
curl -u user1:password1 'http://localhost:8080/reservations' -i -X POST -H 'Content-Type: application/json;charset=UTF-8' -H 'Accept: application/hal+json' -d '{"timeSlot":"NINE_AM_TO_TEN_AM","room":"/rooms/2"}'
{
  "timeSlot" : "TEN_AM_TO_ELEVEN_AM",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/reservations/4"
    },
    "reservation" : {
      "href" : "http://localhost:8080/reservations/4"
    },
    "room" : {
      "href" : "http://localhost:8080/reservations/4/room"
    },
    "organizer" : {
      "href" : "http://localhost:8080/reservations/4/organizer"
    }
  }
}

```
###  Cancel a reservation
 ```
    curl -u user1:password1 'http://localhost:8080/reservations/1' -i -X DELETE
```

### Show room availability
 ```  
 curl -u user1:password1 'http://localhost:8080/rooms/3/reservations'
{
  "_embedded" : {
    "reservations" : [ {
      "timeSlot" : "NINE_AM_TO_TEN_AM",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/reservations/2"
        },
        "reservation" : {
          "href" : "http://localhost:8080/reservations/2"
        },
        "room" : {
          "href" : "http://localhost:8080/reservations/2/room"
        },
        "organizer" : {
          "href" : "http://localhost:8080/reservations/2/organizer"
        }
      }
    }, {
      "timeSlot" : "TEN_AM_TO_ELEVEN_AM",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/reservations/3"
        },
        "reservation" : {
          "href" : "http://localhost:8080/reservations/3"
        },
        "room" : {
          "href" : "http://localhost:8080/reservations/3/room"
        },
        "organizer" : {
          "href" : "http://localhost:8080/reservations/3/organizer"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/rooms/3/reservations"
    }
  }
}      
```
