//package it.gov.pagopa.atmlayer.service.model.repository;
//
//import io.smallrye.mutiny.Uni;
//import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
//import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//class BpmnVersionRepositoryTest {
//
//    @BeforeAll
//    static void initAll() {
//
//    }
//
//    @BeforeEach
//    void init() {
//
//    }
//
//    @Test
//    void findBySHA256() {
//        UUID uuid = UUID.randomUUID();
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setModelVersion(1L);
//        bpmnVersion.setBpmnId(uuid);
//        bpmnVersion.setSha256("sha256");
//        Uni<BpmnVersion> expectedValue = Uni.createFrom().item(bpmnVersion);
//        String sha256 = "sha256";
//
//
//        BpmnVersionRepository bpmnversionrepository = new BpmnVersionRepository();
//        Uni<BpmnVersion> actualValue = bpmnversionrepository.findBySHA256(sha256);
//        Assertions.assertEquals(expectedValue, actualValue);
//    }
//
//    @Test
//    void findByIds() {
//        UUID uuid = UUID.randomUUID();
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setModelVersion(1L);
//        bpmnVersion.setBpmnId(uuid);
//        bpmnVersion.setSha256("sha256");
//        BpmnVersionPK pk1 = new BpmnVersionPK(uuid, 1L);
//
//        UUID uuid2 = UUID.randomUUID();
//        BpmnVersion bpmnVersion2 = new BpmnVersion();
//        bpmnVersion2.setModelVersion(2L);
//        bpmnVersion2.setBpmnId(uuid2);
//        bpmnVersion2.setSha256("sha256_2");
//        BpmnVersionPK pk2 = new BpmnVersionPK(uuid2, 2L);
//
//        Uni<List<BpmnVersion>> expectedValue = Uni.createFrom().item(List.of(bpmnVersion, bpmnVersion2));
//        Set<BpmnVersionPK> ids = Set.of(pk1, pk2);
//
//
//        BpmnVersionRepository bpmnversionrepository = new BpmnVersionRepository();
//        Uni<List<BpmnVersion>> actualValue = bpmnversionrepository.findByIds(ids);
//        System.out.println("Expected Value=" + expectedValue + " . Actual Value=" + actualValue);
//        Assertions.assertEquals(expectedValue, actualValue);
//    }
//
//    @Test
//    void findByIdAndFunction() {
//        UUID uuid = UUID.randomUUID();
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setModelVersion(1L);
//        bpmnVersion.setBpmnId(uuid);
//        bpmnVersion.setSha256("sha256");
//        bpmnVersion.setFunctionType("MENU");
//
//        Uni<List<BpmnVersion>> expectedValue = Uni.createFrom().item(List.of(bpmnVersion));
//        String functionType = "MENU";
//
//
//        BpmnVersionRepository bpmnversionrepository = new BpmnVersionRepository();
//        Uni<List<BpmnVersion>> actualValue = bpmnversionrepository.findByIdAndFunction(uuid, functionType);
//        Assertions.assertEquals(expectedValue, actualValue);
//    }
//
//    @Test
//    void findByDefinitionKey() {
//        UUID uuid = UUID.randomUUID();
//        BpmnVersion bpmnVersion = new BpmnVersion();
//        bpmnVersion.setModelVersion(1L);
//        bpmnVersion.setBpmnId(uuid);
//        bpmnVersion.setSha256("sha256");
//        bpmnVersion.setFunctionType("MENU");
//        bpmnVersion.setDefinitionKey("TEST");
//
//        Uni<BpmnVersion> expectedValue = Uni.createFrom().item(bpmnVersion);
//        String definitionKey = "TEST";
//
//
//        BpmnVersionRepository bpmnversionrepository = new BpmnVersionRepository();
//        Uni<BpmnVersion> actualValue = bpmnversionrepository.findByDefinitionKey(definitionKey);
//        Assertions.assertEquals(expectedValue, actualValue);
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @AfterAll
//    static void tearDownAll() {
//    }
//}
//
