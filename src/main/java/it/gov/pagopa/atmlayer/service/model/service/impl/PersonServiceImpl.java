package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.Person;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerRestException;
import it.gov.pagopa.atmlayer.service.model.repository.PersonRepository;
import it.gov.pagopa.atmlayer.service.model.service.PersonService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class PersonServiceImpl implements PersonService {
    @Inject
    PersonRepository personRepository;

    @Override
    @WithTransaction
    public Uni<Person> createPerson(Person inputPerson) {
        return personRepository.persist(inputPerson)
                .invoke(person -> log.debug("Created User with id: {}", person.id));
    }

    @Override
    @WithSession
    public Uni<List<Person>> getAll() {
        return personRepository.findAll().list();
    }

    @Override
    @WithSession
    public Uni<Person> getByIdOrThrowException(UUID id) {
        return this.getById(id)
                .onItem()
                .ifNull()
                .failWith(() ->
                {
                    String errorMessage = String.format("User with id %s not found", id);
                    log.error(errorMessage);
                    return AtmLayerRestException.builder()
                            .message(errorMessage)
                            .statusCode(Response.Status.NOT_FOUND).build();
                });

    }

    private Uni<Person> getById(UUID id) {
        return personRepository.findById(id);
    }
}
