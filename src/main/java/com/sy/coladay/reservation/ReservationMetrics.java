package com.sy.coladay.reservation;

import com.sy.coladay.company.Companies;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
@Slf4j
public class ReservationMetrics {

  // Do not rename or change the string value unless a change is also applied
  // to the monitoring system.
  private static final String NUMBER_OF_RESERVATIONS = "number_of_reservations";

  private MeterRegistry meterRegistry;
  private Counter cokeNumberOfReservations;
  private Counter pepsiNumberOfReservations;

  public ReservationMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    this.cokeNumberOfReservations = this.meterRegistry.counter(NUMBER_OF_RESERVATIONS, "owner",
                                                               "coke");
    this.pepsiNumberOfReservations = this.meterRegistry.counter(NUMBER_OF_RESERVATIONS, "owner",
                                                            "pepsi");
  }

  @HandleAfterCreate
  void handleNewReservation(Reservation reservation) {

    if (Companies.COKE.equals(reservation.getOrganizer().getCompany())) {
      cokeNumberOfReservations.increment();
    } else if (Companies.PEPSI.equals(reservation.getOrganizer().getCompany())) {
      pepsiNumberOfReservations.increment();
    } else {
      LOG.warn("Could compute the metric for the reservation with id {} and company {}. Metrics "
                   + "export for that company might not be supported. ",
               reservation.getId(),
               reservation.getOrganizer().getCompany().name());
    }
  }

  public MeterRegistry getMeterRegistry() {
    return meterRegistry;
  }

  public Counter getPepsiNumberOfReservations() {
    return pepsiNumberOfReservations;
  }

  public Counter getCokeNumberOfReservations() {
    return cokeNumberOfReservations;
  }
}
