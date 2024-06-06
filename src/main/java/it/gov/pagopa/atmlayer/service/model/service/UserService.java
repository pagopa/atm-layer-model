package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;

import java.util.List;

public interface UserService {

    Uni<User> insertUser(UserInsertionDTO userInsertionDTO);

    Uni<User> updateUser(UserInsertionDTO userInsertionDTO);

    Uni<Boolean> deleteUser(String userId);

    Uni<User> findById(String userId);

    Uni<List<User>> getAllUsers();
}
