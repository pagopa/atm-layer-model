package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.User;

public interface UserService {

    Uni<User> insertUser(User user);

    Uni<User> findById(String userId);
}
