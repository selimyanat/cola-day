package com.sy.coladay.reservation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sy.coladay.room.Room
import com.sy.coladay.user.User
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * Represents a reservation with its core attributes.
 */
@Entity
@Table(
    name = "reservations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["room_id", "time_slot"])]
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Reservation (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "time_slot", length = 40, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "time slot cannot be null")
    var timeSlot: TimeSlots,

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne
    @NotNull(message = "room cannot be null")
    var room: Room,

    @OneToOne
    @JoinColumn(name = "organizer_id", nullable = false, updatable = false)
    @JsonIgnore
    @NotNull(message = "organizer cannot be null")
    var organizer:  User) {


    override fun hashCode(): Int {
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return javaClass.hashCode();
    }

    override fun equals(other: Any?): Boolean {
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        if (this === other) return true

        if (other !is Reservation) return false

        return id != null && id == other.id
    }
}