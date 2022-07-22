package com.sy.coladay.room

import com.sy.coladay.company.Companies
import com.sy.coladay.reservation.Reservation
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * Represents a room with its core attributes.
 *
 * @author selim
 */
@Table(name = "rooms")
@Entity
data class Room (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(length = 9, nullable = false, unique = true)
    @NotEmpty(message = "name cannot be null or empty")
    var name: String?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "company cannot be null")
    val owner:  Companies?,

    @OneToMany(mappedBy = "room")
    val reservations: List<Reservation>?) {

    override fun toString(): String {
        return ("$name")
    }

    override fun hashCode(): Int {
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return javaClass.hashCode();
    }

    override fun equals(other: Any?): Boolean {
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        if (this === other) return true

        if (other !is Room) return false

        return id != null && id == other.id
    }

}