package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;

import java.util.List;

public interface UserService {

    Uni<User> insertUser(UserInsertionDTO userInsertionDTO);

    Uni<User> insertUserWithProfiles(UserInsertionWithProfilesDTO userInsertionWithProfilesDTO);

    Uni<User> findUser(String userId);

    Uni<User> updateUser(UserInsertionDTO userInsertionDTO);

    Uni<Boolean> deleteUser(String userId);

    Uni<User> getById(String userId);

    Uni<List<User>> getAllUsers();

    Uni<Long> countUsers();

    Uni<Void> checkFirstAccess(String userId);
}