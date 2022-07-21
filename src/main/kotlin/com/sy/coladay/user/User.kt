package com.sy.coladay.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sy.coladay.company.Companies
import liquibase.pro.packaged.o
import java.awt.print.Book
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

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    open val id: Long? = null,

//    @Column(nullable = false, unique = true)
//    @NotEmpty(message = "name cannot be null or empty")
//    open val name:  String? = null,

//    @Column(nullable = false)
//    @JsonIgnore
//    @NotEmpty(message = "password cannot be null or empty")
//    open val password:String,

//    @Column(length = 20, nullable = false)
//    @Enumerated(EnumType.STRING)
//    open val company: Companies,

//    @Column(length = 10, nullable = false)
//    @Enumerated(EnumType.STRING)
//    @JsonIgnore
//    open val role: Role = Role.USER,

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

        if (other !is Book) return false

        val anotherUser = (other as User)

        return id != null && id == anotherUser.id
    }
}