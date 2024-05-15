package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class UserMapper {

    public abstract UserDTO toDTO(User user);

    public User toEntityInsertion(String userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }
}
