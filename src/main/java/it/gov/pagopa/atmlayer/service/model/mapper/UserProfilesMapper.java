package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesDTO;
import it.gov.pagopa.atmlayer.service.model.dto.UserProfilesInsertionDTO;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfiles;
import it.gov.pagopa.atmlayer.service.model.entity.UserProfilesPK;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class UserProfilesMapper {

    @Mapping(source = "userProfilesPK.userId", target = "userId")
    @Mapping(source = "userProfilesPK.profileId", target = "profileId")
    public abstract UserProfilesDTO toDTO(UserProfiles userProfiles);

    public List<UserProfiles> toEntityInsertion(UserProfilesInsertionDTO userProfilesInsertionDTO) {
        List<UserProfiles> userProfilesList = new ArrayList<>();
        for (Integer profileId : userProfilesInsertionDTO.getProfileIds()) {
            UserProfiles userProfiles = new UserProfiles();
            UserProfilesPK userProfilesPK = new UserProfilesPK(userProfilesInsertionDTO.getUserId(), profileId);
            userProfiles.setUserProfilesPK(userProfilesPK);
            userProfilesList.add(userProfiles);
        }
        return userProfilesList;
    }
}
