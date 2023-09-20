package it.gov.pagopa.atml.mil.integration.resource;

import io.quarkus.arc.properties.UnlessBuildProperty;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atml.mil.integration.dto.PersonDto;
import it.gov.pagopa.atml.mil.integration.mapper.PersonMapper;
import it.gov.pagopa.atml.mil.integration.service.PersonService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Resource class that expose the API to retrieve info about the service
 */
@Path("/person")
@Tag(name = "Person", description = "Person operations")
@UnlessBuildProperty(name = "app.execution-mode", stringValue = "WRITE", enableIfMissing = true)
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public class PersonResourceRead {

    private final Logger logger = LoggerFactory.getLogger(PersonResourceRead.class);

    @Inject
    PersonService personService;

    @Inject
    PersonMapper personMapper;

    @GET
    public Uni<List<PersonDto>> getAllPersons() {
        return personService.getAll()
                .map(people -> personMapper.toDtoList(people));
    }

    @GET
    @Path("/{id}")
    public Uni<PersonDto> getPersonById(@PathParam("id") UUID id) {
        return personService.getByIdOrThrowException(id)
                .map(person -> personMapper.toDto(person));
    }
}
