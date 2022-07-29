package com.sy.coladay.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sy.coladay.company.Companies
import javax.persistence.*
import javax.validation.constraints.NotEmpty


/**
 * Represents a user with its core attributes.
 *
 * @author selim
 */
@Table(name = "users")
@Entity
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "name cannot be null or empty")
    var name:  String?,

    @Column(nullable = false)
    @JsonIgnore
    @NotEmpty(message = "password cannot be null or empty")
    var password:String,

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var company: Companies,

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    var role: Role = Role.USER)  {


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

        if (other !is User) return false

        return id != null && id == other.id
    }
}