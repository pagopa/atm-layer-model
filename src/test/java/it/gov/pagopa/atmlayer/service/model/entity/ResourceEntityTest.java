package it.gov.pagopa.atmlayer.service.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.enumeration.NoDeployableResourceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ResourceEntityTest {

  @Test
  public void testResourceIdSetterGetter() {
    ResourceEntity resourceEntity = new ResourceEntity();
    UUID uuid = UUID.randomUUID();
    resourceEntity.setResourceId(uuid);
    assertEquals(uuid, resourceEntity.getResourceId());
  }

  @Test
  public void testSha256SetterGetter() {
    ResourceEntity resourceEntity = new ResourceEntity();
    String sha256 = "sampleSha256Hash";
    resourceEntity.setSha256(sha256);
    assertEquals(sha256, resourceEntity.getSha256());
  }

  @Test
  public void testNoDeployableResourceTypeSetterGetter() {
    ResourceEntity resourceEntity = new ResourceEntity();
    NoDeployableResourceType resourceType = NoDeployableResourceType.HTML;
    resourceEntity.setNoDeployableResourceType(resourceType);
    assertEquals(resourceType, resourceEntity.getNoDeployableResourceType());
  }

  @Test
  public void testCreatedAtNotNullAfterPersist() {
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntity.generateUUID();
    assertNull(resourceEntity.getCreatedAt());
  }

  @Test
  public void testLastUpdatedAtAfterUpdate() {
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntity.generateUUID();
    assertNull(resourceEntity.getLastUpdatedAt());
  }

  @Test
  public void testGenerateUUID() {
    ResourceEntity resourceEntity = new ResourceEntity();
    UUID randomUUIDResult = UUID.randomUUID();
    resourceEntity.setResourceId(randomUUIDResult);
    resourceEntity.generateUUID();
    assertSame(randomUUIDResult, resourceEntity.getResourceId());
  }

  @Test
  public void testCdnUrl() {
    ResourceEntity resourceEntity = new ResourceEntity();
    ResourceFile resourceFile = new ResourceFile();
    resourceFile.setStorageKey("sampleStorageKey");
    resourceEntity.setResourceFile(resourceFile);
    String cdnBaseUrl = "http://cdn.example.com";
    String cdnOffsetPath = "/offset/path";
    System.setProperty("cdn.base-url", cdnBaseUrl);
    System.setProperty("cdn.offset-path", cdnOffsetPath);
    String expectedCdnUrl =
        cdnBaseUrl + "/" + resourceFile.getStorageKey().substring(cdnOffsetPath.length() + 1);
    assertNotEquals(expectedCdnUrl, resourceEntity.getCdnUrl());
  }
}

