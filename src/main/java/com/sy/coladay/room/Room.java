package com.sy.coladay.room;

import com.sy.coladay.company.Companies;
import com.sy.coladay.reservation.Reservation;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a room with its core attributes.
 *
 * @author  selim
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "rooms")
@Entity
public class Room implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 9, nullable = false, unique = true)
  @NotEmpty(message = "name cannot be null or empty")
  private String name;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "company cannot be null")
  private Companies owner;

  @OneToMany(mappedBy = "room")
  private List<Reservation> reservations;

}
