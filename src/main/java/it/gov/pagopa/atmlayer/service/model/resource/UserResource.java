package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.repository.UserRepository;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@ApplicationScoped
@Path("/users")
@Tag(name = "User")
@Slf4j
public class UserResource {

    @Inject
    UserMapper userMapper;

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserWithProfilesDTO> insert(@RequestBody(required = true) @Valid UserInsertionDTO userInsertionDTO) {
        return this.userService.insertUser(userInsertionDTO)
                .onItem()
                .transformToUni(insertedUser -> Uni.createFrom().item(this.userMapper.toProfilesDTO(insertedUser)));
    }

    @POST
    @Path("/first-access/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserWithProfilesDTO> firstAccess(@PathParam("userId") String userId) {
        return this.userService.checkFirstAccess(userId)
                .onItem()
                .transformToUni(insertedProfiles -> userService.findUser(userId))
                .onItem()
                .transformToUni(user -> Uni.createFrom().item(this.userMapper.toProfilesDTO(user)));
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserWithProfilesDTO> update(@RequestBody(required = true) @Valid UserInsertionDTO userInsertionDTO) {
        return this.userService.updateUser(userInsertionDTO)
                .onItem()
                .transformToUni(updatedUser -> Uni.createFrom().item(userMapper.toProfilesDTO(updatedUser)));
    }


    @POST
    @Path("/insert-with-profiles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserWithProfilesDTO> insertWithProfiles(@RequestBody(required = true) @Valid UserInsertionWithProfilesDTO userInsertionWithProfilesDTO) {
        return this.userService.insertUserWithProfiles(userInsertionWithProfilesDTO)
                .onItem()
                .transformToUni(insertedProfiles -> userRepository.findByIdCustom(userInsertionWithProfilesDTO.getUserId()))
                .onItem()
                .transformToUni(insertedUser -> Uni.createFrom().item(this.userMapper.toProfilesDTO(insertedUser)));
    }


    @DELETE
    @Path("/delete/userId/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Void> delete(@PathParam("userId") String userId) {
        return this.userService.deleteUser(userId)
                .onItem()
                .ignore()
                .andSwitchTo(Uni.createFrom().voidItem());
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserWithProfilesDTO> getByIdWithProfiles(@PathParam("userId") String userId) {
        return this.userService.getById(userId)
                .onItem()
                .transform(foundUser -> userMapper.toProfilesDTO(foundUser));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserWithProfilesDTO>> getAll() {
        return this.userService.getAllUsers()
                .onItem()
                .transform(Unchecked.function(list -> {
                    if (list.isEmpty()) {
                        log.info("There is not any user saved in database!");
                    }
                    return userMapper.toDTOList(list);
                }));
    }
}