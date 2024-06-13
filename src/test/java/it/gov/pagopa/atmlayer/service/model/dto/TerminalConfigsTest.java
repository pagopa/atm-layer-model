package it.gov.pagopa.atmlayer.service.model.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TerminalConfigsTest {


  @Test
  void testConstructor() {
    TerminalConfigs actualTerminalConfigs = new TerminalConfigs();
    UUID randomUUIDResult = UUID.randomUUID();
    actualTerminalConfigs.setTemplateId(randomUUIDResult);
    actualTerminalConfigs.setTemplateVersion(1L);
    ArrayList<String> stringList = new ArrayList<>();
    actualTerminalConfigs.setTerminalIds(stringList);
    actualTerminalConfigs.toString();
    assertSame(randomUUIDResult, actualTerminalConfigs.getTemplateId());
    assertEquals(1L, actualTerminalConfigs.getTemplateVersion().longValue());
    assertSame(stringList, actualTerminalConfigs.getTerminalIds());
  }

  @Test
  void testEquals() {
    TerminalConfigs terminalConfigs = new TerminalConfigs();
    terminalConfigs.setTemplateId(UUID.randomUUID());
    terminalConfigs.setTemplateVersion(1L);
    terminalConfigs.setTerminalIds(new ArrayList<>());
    assertNotEquals(null, terminalConfigs);
  }
}

