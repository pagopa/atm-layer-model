package it.gov.pagopa.atml.mil.integration.mapper;

import it.gov.pagopa.atml.mil.integration.dto.PersonDto;
import it.gov.pagopa.atml.mil.integration.entity.Person;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PersonMapper {
    PersonDto toDto(Person person);

    List<PersonDto> toDtoList(List<Person> personList);

}
