package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.Person;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class PersonRepository implements PanacheRepositoryBase<Person, UUID> {

    public Uni<Person> findByName(String name) {
        return find("name", name).firstResult();
    }

}
