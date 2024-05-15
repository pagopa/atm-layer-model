package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfileRepository implements PanacheRepositoryBase<Profile, Integer> {
}
