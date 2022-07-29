package com.sy.coladay.reservation

import com.sy.coladay.company.Companies
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.annotation.HandleAfterCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
@RepositoryEventHandler
open class ReservationMetrics @Autowired constructor(private val meterRegistry: MeterRegistry) {

    var cokeNumberOfReservations: Counter by Delegates.notNull()

    var pepsiNumberOfReservations: Counter by Delegates.notNull()

    init {
        this.cokeNumberOfReservations = this.meterRegistry.counter(
            NUMBER_OF_RESERVATIONS, "owner",
            "coke")
        this.pepsiNumberOfReservations = this.meterRegistry.counter(
            NUMBER_OF_RESERVATIONS, "owner",
            "pepsi")
    }


    @HandleAfterCreate
    fun handleNewReservation(reservation: Reservation) {
        if (Companies.COKE == reservation.organizer.company) {
            cokeNumberOfReservations.increment()
        } else if (Companies.PEPSI == reservation.organizer.company) {
            pepsiNumberOfReservations.increment()
        } else {
            LOG.warn(
                "Could not compute the metric for the reservation with id {} and company {}. " +
                        "Metrics "
                        + "export for that company might not be supported. ",
                reservation.id,
                reservation.organizer.company.name
            )
        }
    }

    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(ReservationMetrics::class.java)

        // Do not rename or change the string value unless a change is also applied
        // to the monitoring system.
        private val NUMBER_OF_RESERVATIONS: String = "number_of_reservations"
    }
}