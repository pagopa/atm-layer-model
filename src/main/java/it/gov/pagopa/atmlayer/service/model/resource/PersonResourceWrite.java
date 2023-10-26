package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.arc.properties.UnlessBuildProperty;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.PersonDto;
import it.gov.pagopa.atmlayer.service.model.entity.Person;
import it.gov.pagopa.atmlayer.service.model.mapper.PersonMapper;
import it.gov.pagopa.atmlayer.service.model.service.PersonService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;

/**
 * Resource class that expose the API to retrieve info about the service
 */
@Path("/person")
@Tag(name = "Person", description = "Person operations")
@UnlessBuildProperty(name = "app.execution-mode", stringValue = "READ", enableIfMissing = true)
public class PersonResourceWrite {

    private final Logger logger = LoggerFactory.getLogger(PersonResourceWrite.class);

    @Inject
    PersonService personService;

    @Inject
    PersonMapper personMapper;

    @POST
    public Uni<PersonDto> createPerson() {

        Person person = new Person();
        person.firstName = "Stef";
        person.lastName = "Brazov";
        person.birth = LocalDate.of(1910, Month.FEBRUARY, 1);

        return personService.createPerson(person)
                .map(personEntity -> personMapper.toDto(personEntity));
    }
}
