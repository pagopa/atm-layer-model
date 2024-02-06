package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfileDto;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfile;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserProfileMapper {

    @Mapping(source = "userProfile.userId", target = "userId")
    @Mapping(expression = "java(getEnumValue(userProfile.getProfile()))", target = "profile")
    @Mapping(source = "userProfile.createdAt", target = "createdAt")
    @Mapping(source = "userProfile.lastUpdatedAt", target = "lastUpdatedAt")
    public abstract UserProfileDto toUserProfileDto(UserProfile userProfile);
    @Mapping(source = "userProfile.userId", target = "userId")
    @Mapping(source = "userProfile.profile", target = "profile")
    @Mapping(source = "userProfile.createdAt", target = "createdAt")
    @Mapping(source = "userProfile.lastUpdatedAt", target = "lastUpdatedAt")
    public abstract UserProfile toUserProfile(UserProfileCreationDto userProfile);

    UserProfileEnum getEnumValue(int source) {
        return UserProfileEnum.valueOf(source);
    }

    public UserProfileDto toUserProfileDtoWithProfileMapping(UserProfile userProfile) {
        UserProfileDto userProfileDTO = this.toUserProfileDto(userProfile);
        switch(UserProfileEnum.valueOf(userProfile.getProfile())){
            case GUEST -> {
                userProfileDTO.setVisible(true);
                userProfileDTO.setEditable(false);
                userProfileDTO.setAdmin(false);
            }
            case OPERATOR -> {
                userProfileDTO.setVisible(true);
                userProfileDTO.setEditable(true);
                userProfileDTO.setAdmin(false);
            }
            case ADMIN -> {
                userProfileDTO.setVisible(true);
                userProfileDTO.setEditable(true);
                userProfileDTO.setAdmin(true);
            }
        }
        return userProfileDTO;
    }

    public List<UserProfileDto> toDtoList(List<UserProfile> list){
        return list.stream().map( x -> this.toUserProfileDto(x)).toList();
    }
}
