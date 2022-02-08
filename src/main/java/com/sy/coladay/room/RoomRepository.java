package com.sy.coladay.room;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository backing the <code>Room</code>.entity.
 *
 * @author selim
 */

@RepositoryRestResource(collectionResourceRel = "rooms", path = "rooms")
public interface RoomRepository extends PagingAndSortingRepository<Room, Long> {

  @RestResource(exported = false)
  void deleteById(Long id);

  @RestResource(exported = false)
  Room save(Room entity);

}
