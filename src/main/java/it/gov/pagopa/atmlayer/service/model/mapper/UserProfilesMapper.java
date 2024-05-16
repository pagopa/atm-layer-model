package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class UserProfilesMapper {

    @Mapping(source = "userProfilesPK.userId", target = "userId")
    @Mapping(source = "userProfilesPK.profileId", target = "profileId")
    public abstract UserProfilesDTO toDTO(UserProfiles userProfiles);

    public UserProfiles toEntityInsertion(UserProfilesInsertionDTO userProfilesInsertionDTO) {
        UserProfiles userProfiles = new UserProfiles();
        UserProfilesPK userProfilesPK = new UserProfilesPK(userProfilesInsertionDTO.getUserId(), userProfilesInsertionDTO.getProfileId());
        userProfiles.setUserProfilesPK(userProfilesPK);
        return userProfiles;
    }
}
