package com.sy.coladay.user

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.stereotype.Repository
import java.util.*
import javax.validation.constraints.NotEmpty

/**
 * Repository backing the `User`.entity.
 *
 * @author selim
 */
@Repository
interface UserRepository : CrudRepository<User?, Long?> {
    @RestResource(exported = false)
    override fun deleteById(id: Long?)

    @RestResource(exported = false)
    fun findByName(name: @NotEmpty String?): Optional<User?>?

    @RestResource(exported = false)
    fun save(entity: User): User
}