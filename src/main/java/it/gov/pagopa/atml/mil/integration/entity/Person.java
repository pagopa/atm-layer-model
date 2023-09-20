package it.gov.pagopa.atml.mil.integration.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Person extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;
    public String firstName;
    public String lastName;
    public LocalDate birth;
}