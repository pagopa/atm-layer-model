package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class UserMapper {

    public abstract UserDTO toDTO(User user);

    public User toEntityInsertion(String userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    public List<UserDTO> toDTOList(List<User> list) {
        return list.stream().map(this::toDTO).toList();
    }
}
