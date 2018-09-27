package com.coladay.reservation;

import com.coladay.room.Room;
import com.coladay.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a reservation with its core attributes.
 */
@AllArgsConstructor
@Data
@Entity
@Table(name = "reservations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_id", "time_slot"})})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reservation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "time_slot", length = 40, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "time slot cannot be null")
  private TimeSlots timeSlot;

  @JoinColumn(name = "room_id", nullable = false)
  @ManyToOne
  @NotNull(message = "room cannot be null")
  private Room room;

  @OneToOne
  @JoinColumn(name = "organizer_id", nullable = false, updatable = false)
  @NotNull(message = "organizer cannot be null")
  @JsonIgnore
  private User organizer;

}
