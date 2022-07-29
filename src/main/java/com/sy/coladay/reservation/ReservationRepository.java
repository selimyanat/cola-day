package com.sy.coladay.reservation;

import com.sy.coladay.company.Companies;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository backing the <code>Reservation</code>.entity. Note that this repository can't be
 * transitioned to kotlin as @{@link RestResource} is not fully supported. The exported flag is
 * not working as expected in the current version of spring data rest (2.7.0) - moving to 3.0.0
 * when ready(November 2022)
 *
 * @see https://docs.spring.io/spring-data/rest/docs/current/changelog.txt
 * @see https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0
 *
 * @author selim
 */
@RepositoryRestResource(collectionResourceRel = "reservations", path = "reservations")
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

  Long countByOrganizerCompany(Companies companies);


}