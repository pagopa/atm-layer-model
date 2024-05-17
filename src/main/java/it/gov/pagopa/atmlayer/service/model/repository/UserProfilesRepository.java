package it.gov.pagopa.atmlayer.service.model.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserProfilesRepository implements PanacheRepositoryBase<UserProfiles, UserProfilesPK> {

}
