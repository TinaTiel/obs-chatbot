package com.tinatiel.obschatbot.data;

import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {
  @Id
  @GeneratedValue
  private UUID id;
}
