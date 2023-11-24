package it.gov.pagopa.atmlayer.service.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LinkDtoTest {

  @Test
  void testNoArgsConstructor() {
    LinkDto actualLinkDto = new LinkDto();
    actualLinkDto.setHref("Href");
    actualLinkDto.setMethod("Method");
    actualLinkDto.setRel("Rel");
    assertEquals("Href", actualLinkDto.getHref());
    assertEquals("Method", actualLinkDto.getMethod());
    assertEquals("Rel", actualLinkDto.getRel());
  }

  @Test
  void testAllArgsConstructor() {
    LinkDto actualLinkDto = new LinkDto("Method", "Href", "Rel");
    actualLinkDto.setHref("Href");
    actualLinkDto.setMethod("Method");
    actualLinkDto.setRel("Rel");
    assertEquals("Href", actualLinkDto.getHref());
    assertEquals("Method", actualLinkDto.getMethod());
    assertEquals("Rel", actualLinkDto.getRel());
  }
}

