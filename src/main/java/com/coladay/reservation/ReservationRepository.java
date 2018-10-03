package com.coladay.reservation;

import com.coladay.company.Companies;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository backing the <code>Reservation</code>.entity.
 * @author selim
 */
@RepositoryRestResource(collectionResourceRel = "reservations", path = "reservations")
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

  Long countByOrganizerCompany(Companies companies);



}