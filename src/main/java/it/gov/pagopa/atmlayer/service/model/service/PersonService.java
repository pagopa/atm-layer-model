package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.Person;

import java.util.List;
import java.util.UUID;


public interface PersonService {


    Uni<Person> createPerson(Person inputPerson);

    Uni<List<Person>> getAll();

    Uni<Person> getByIdOrThrowException(UUID id);
}
