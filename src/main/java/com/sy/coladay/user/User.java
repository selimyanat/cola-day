package com.sy.coladay.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sy.coladay.company.Companies;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a user with its core attributes.
 *
 * @author selim
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
@Entity
@ToString(exclude = "password")
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  @NotEmpty(message = "name cannot be null or empty")
  private String name;

  @Column(nullable = false)
  @JsonIgnore
  @NotEmpty(message = "password cannot be null or empty")
  private String password;

  @Column(length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "company cannot be null")
  private Companies company;

  @Column(length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "role cannot be null")
  @JsonIgnore
  private Role role;

}
