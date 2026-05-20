package com.example.identityservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String userName;
  String passWord;
  String firstName;
  String lastName;
  LocalDate dob;

  @ManyToMany Set<Role> roles;
}
