package it.gov.pagopa.atmlayer.service.model.service;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.List;

public interface UserService {

    Uni<User> insertUser(UserInsertionDTO userInsertionDTO);

    Uni<User> insertUserWithProfiles(UserInsertionWithProfilesDTO userInsertionWithProfilesDTO);

    Uni<User> findUser(String userId);

    Uni<User> updateUser(@NotBlank String userId, @NotBlank String name, @NotBlank String surname);

    Uni<User> updateWithProfiles(UserInsertionWithProfilesDTO userInsertionWithProfilesDTO);

    Uni<Boolean> deleteUser(String userId);

    Uni<User> getById(String userId);

    Uni<List<User>> getAllUsers();

    Uni<PageInfo<User>> getUserFiltered(int pageIndex, int pageSize, String name, String surname, String userId);

    Uni<Long> countUsers();

    Uni<Void> checkFirstAccess(String userId);
}
