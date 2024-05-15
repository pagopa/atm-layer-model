package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.UserDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.mapper.UserMapper;
import it.gov.pagopa.atmlayer.service.model.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@ApplicationScoped
@Path("/user")
@Tag(name = "User")
@Slf4j
public class UserResource {

    @Inject
    UserMapper userMapper;

    @Inject
    UserService userService;

    @POST
    @Path("/insert/userId/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserDTO> insert(@PathParam("userId") String userId) {
        User user = userMapper.toEntityInsertion(userId);
        return this.userService.insertUser(user)
                .onItem()
                .transformToUni(insertedUser -> Uni.createFrom().item(this.userMapper.toDTO(insertedUser)));
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
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<UserDTO>> getAll() {
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
