package it.gov.pagopa.atml.mil.integration.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atml.mil.integration.entity.Person;

import java.util.List;
import java.util.UUID;


public interface PersonService {


    Uni<Person> createPerson(Person inputPerson);

    Uni<List<Person>> getAll();

    Uni<Person> getByIdOrThrowException(UUID id);
}
