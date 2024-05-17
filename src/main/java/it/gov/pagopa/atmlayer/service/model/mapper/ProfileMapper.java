package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.ProfileCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.Profile;
import it.gov.pagopa.atmlayer.service.model.model.ProfileDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public abstract class ProfileMapper {
    public abstract Profile toEntity(ProfileCreationDto profileCreationDto);

    public abstract ProfileDTO toDto(Profile profile);

    public List<ProfileDTO> toDTOList(List<Profile> list) {
        return list.stream().map(this::toDto).toList();
    }
}
