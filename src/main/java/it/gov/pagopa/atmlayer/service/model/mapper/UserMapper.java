package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import jakarta.inject.Inject;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public abstract class UserMapper {
    @Inject
    ProfileMapper profileMapper;

    public User toEntityInsertion(UserInsertionDTO userInsertionDTO) {
        User user = new User();
        user.setUserId(userInsertionDTO.getUserId());
        user.setName(userInsertionDTO.getName());
        user.setSurname(userInsertionDTO.getSurname());
        return user;
    }

    @IterableMapping(qualifiedByName = "toProfilesDTO")
    public abstract List<UserWithProfilesDTO> toDTOList(List<User> list);

    @Mapping(source = "userProfiles", target = "profiles", qualifiedByName = "toProfileDTOList")
    @Named("toProfilesDTO")
    public UserWithProfilesDTO toProfilesDTO(User user) {
        if (user == null) {
            return null;
        }

        UserWithProfilesDTO userWithProfilesDTO = new UserWithProfilesDTO();
        userWithProfilesDTO.setUserId(user.getUserId());
        userWithProfilesDTO.setName(user.getName());
        userWithProfilesDTO.setSurname(user.getSurname());
        userWithProfilesDTO.setCreatedAt(user.getCreatedAt());
        userWithProfilesDTO.setLastUpdatedAt(user.getLastUpdatedAt());
        userWithProfilesDTO.setProfiles(toProfileDTOList(user.getUserProfiles()));

        return userWithProfilesDTO;
    }

    @Named("toProfileDTOList")
    List<ProfileDTO> toProfileDTOList(List<UserProfiles> userProfiles) {
        if (userProfiles == null) {
            return null;
        }
        return userProfiles.stream()
                .map(up -> profileMapper.toDto(up.getProfile()))
                .collect(Collectors.toList());
    }

}
