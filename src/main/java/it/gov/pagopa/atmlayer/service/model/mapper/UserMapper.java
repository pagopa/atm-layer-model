package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserInsertionWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserWithProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.User;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import it.gov.pagopa.atmlayer.service.model.model.BpmnFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import jakarta.inject.Inject;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
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

    public User toEntityInsertionWithProfiles(UserInsertionWithProfilesDTO userInsertionWithProfilesDTO) {
        User user = new User();
        List<UserProfiles> userProfilesList = new ArrayList<>();
        user.setUserId(userInsertionWithProfilesDTO.getUserId());
        user.setName(userInsertionWithProfilesDTO.getName());
        user.setSurname(userInsertionWithProfilesDTO.getSurname());
        userInsertionWithProfilesDTO.getProfileIds().forEach(x -> userProfilesList.add(new UserProfiles(new UserProfilesPK(user.getUserId(), x))));
        user.setUserProfiles(userProfilesList);
        return user;
    }

    @IterableMapping(qualifiedByName = "toProfilesDTO")
    @Named("toDTOList")
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

    @Mapping(source = "results", target = "results", qualifiedByName = "toDTOList")
    public abstract PageInfo<UserWithProfilesDTO> toDTOListPaged(PageInfo<User> input);

}
