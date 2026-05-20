package com.example.identityservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {
  @Id String id;
  Date expiryTime;
}
