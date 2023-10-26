package it.gov.pagopa.atmlayer.service.model.mapper;

import it.gov.pagopa.atmlayer.service.model.dto.PersonDto;
import it.gov.pagopa.atmlayer.service.model.entity.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PersonMapper {
    PersonDto toDto(Person person);

    List<PersonDto> toDtoList(List<Person> personList);

}
