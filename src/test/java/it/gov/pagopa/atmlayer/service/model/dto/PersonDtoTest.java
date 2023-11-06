package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class PersonDtoTest {

  @Test
  void testCanEqual() {
    assertFalse((new PersonDto()).canEqual("Other"));
  }

  @Test
  void testNoArgsConstructor() {
    PersonDto actualPersonDto = new PersonDto();
    actualPersonDto.setFirstName("name");
    UUID randomUUIDResult = UUID.randomUUID();
    actualPersonDto.setId(randomUUIDResult);
    actualPersonDto.setLastName("surname");
    actualPersonDto.toString();
    assertEquals("name", actualPersonDto.getFirstName());
    assertSame(randomUUIDResult, actualPersonDto.getId());
    assertEquals("surname", actualPersonDto.getLastName());
  }

  @Test
  void testAllArgsConstructor() {
    PersonDto actualPersonDto = new PersonDto(UUID.randomUUID(), "name", "surname");
    actualPersonDto.setFirstName("name");
    UUID randomUUIDResult = UUID.randomUUID();
    actualPersonDto.setId(randomUUIDResult);
    actualPersonDto.setLastName("surname");
    actualPersonDto.toString();
    assertEquals("name", actualPersonDto.getFirstName());
    assertSame(randomUUIDResult, actualPersonDto.getId());
    assertEquals("surname", actualPersonDto.getLastName());
  }

  @Test
  void testEquals() {
    PersonDto personDto = new PersonDto();
    assertEquals(personDto, personDto);
    int expectedHashCodeResult = personDto.hashCode();
    assertEquals(expectedHashCodeResult, personDto.hashCode());
  }
}

