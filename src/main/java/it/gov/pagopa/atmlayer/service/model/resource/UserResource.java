package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<UserDTO> insert(@PathParam("userId") String userId) {
        User user = userMapper.toEntityInsertion(userId);
        return this.userService.insertUser(user)
                .onItem()
                .transformToUni(insertedUser -> Uni.createFrom().item(this.userMapper.toDTO(insertedUser)));
    }
}
