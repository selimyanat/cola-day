package com.sy.coladay.user;

import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository backing the <code>User</code>.entity.
 * @author selim
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  @RestResource(exported = false)
  void deleteById(Long id);

  @RestResource(exported = false)
  Optional<User> findByName(@NotEmpty String name);

  @RestResource(exported = false)
  User save(User entity);
}
