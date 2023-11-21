package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import it.gov.pagopa.atmlayer.service.model.enumeration.S3ResourceTypeEnum;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.service.impl.BpmnBankConfigService;
import it.gov.pagopa.atmlayer.service.model.service.impl.BpmnFileStorageServiceImpl;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestMulti;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.vertx.core.buffer.Buffer.buffer;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class BpmnResourceTest {
    @InjectMock
    BpmnVersionService bpmnVersionService;
    @InjectMock
    BpmnVersionMapper bpmnVersionMapper;
    @InjectMock
    BpmnEntityValidator bpmnEntityValidator;
    @InjectMock
    BpmnConfigMapper bpmnConfigMapper;
    @InjectMock
    BpmnFileStorageServiceImpl bpmnFileStorageService;
    @InjectMock
    BpmnBankConfigService bpmnBankConfigService;

    @Test
    void testGetAllBpmnOK() {
        List<BpmnVersion> bpmnList = new ArrayList<>();
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnList.add(bpmnVersion);
        List<BpmnDTO> dtoList = new ArrayList<>();
        BpmnDTO bpmnDTO = new BpmnDTO();
        dtoList.add(bpmnDTO);
        when(bpmnVersionService.getAll()).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapper.toDTOList(any(ArrayList.class))).thenReturn(dtoList);
        ArrayList result = given()
                .when().get("/api/v1/model/bpmn")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ArrayList.class);
        assertEquals(1, result.size());
        verify(bpmnVersionService, times(1)).getAll();
        verify(bpmnVersionMapper, times(1)).toDTOList(bpmnList);
    }

    @Test
    void testGetAllBpmnEmptyList() {
        List<BpmnVersion> bpmnList = new ArrayList<>();
        BpmnVersion bpmnVersion = new BpmnVersion();
        bpmnList.add(bpmnVersion);
        List<BpmnDTO> dtoList = new ArrayList<>();
        when(bpmnVersionService.getAll()).thenReturn(Uni.createFrom().item(bpmnList));
        when(bpmnVersionMapper.toDTOList(any(ArrayList.class))).thenReturn(dtoList);
        ArrayList result = given()
                .when().get("/api/v1/model/bpmn")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(ArrayList.class);
        Assertions.assertTrue(result.isEmpty());
        verify(bpmnVersionService, times(1)).getAll();
        verify(bpmnVersionMapper, times(1)).toDTOList(bpmnList);
    }

    @Test
    void testGetEncodedFileOK() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        Long version = 1L;
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.setModelVersion(version);
        BpmnDTO bpmnDTO = new BpmnDTO();
        bpmnDTO.setDeployedFileName("testDTO");
        when(bpmnVersionService.findByPk(new BpmnVersionPK(uuid, version))).thenReturn(Uni.createFrom().item(Optional.ofNullable(bpmnVersion)));
        when(bpmnVersionMapper.toDTO(bpmnVersion)).thenReturn(bpmnDTO);
        given()
                .pathParam("bpmnId", uuid)
                .pathParam("version", version)
                .when().get("/api/v1/model/bpmn/{bpmnId}/version/{version}")
                .then()
                .statusCode(200)
                .body("deployedFileName", is("testDTO"));
    }

    @Test
    void testGetEncodedFileFileNotFound() {
        BpmnVersion bpmnVersion = new BpmnVersion();
        UUID uuid = UUID.randomUUID();
        Long version = 1L;
        bpmnVersion.setBpmnId(uuid);
        bpmnVersion.setModelVersion(version);
        BpmnDTO bpmnDTO = new BpmnDTO();
        bpmnDTO.setDeployedFileName("testDTO");
        when(bpmnVersionService.findByPk(new BpmnVersionPK(uuid, version))).thenReturn(Uni.createFrom().item(Optional.ofNullable(null)));
        when(bpmnVersionMapper.toDTO(bpmnVersion)).thenReturn(bpmnDTO);
        given()
                .pathParam("bpmnId", uuid)
                .pathParam("version", version)
                .when().get("/api/v1/model/bpmn/{bpmnId}/version/{version}")
                .then()
                .statusCode(404);
    }

    @Test
    void testAssociateBPMNOK() {
        BpmnAssociationDto bpmnAssociationDto = getBpmnAssociationDtoInstance();
        List<BpmnBankConfig> bankConfigList = new ArrayList<>();
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        bankConfigList.add(bpmnBankConfig);
        Collection<BpmnBankConfigDTO> dtoList = new HashSet<>();
        BpmnBankConfigDTO bpmnBankConfigDTO = new BpmnBankConfigDTO();
        dtoList.add(bpmnBankConfigDTO);
        String acquirerId = "testAcq";
        String functionType = "MENU";
        when(bpmnEntityValidator.validateExistenceStatusAndFunctionType(any(), any())).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionService.putAssociations(any(), any(), any())).thenReturn(Uni.createFrom().item(bankConfigList));
        when(bpmnConfigMapper.toDTOList(bankConfigList)).thenReturn(dtoList);
        given()
                .pathParam("acquirerId", acquirerId)
                .pathParam("functionType", functionType)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bpmnAssociationDto)
                .when().put("api/v1/model/bpmn/bank/{acquirerId}/associations/function/{functionType}")
                .then()
                .statusCode(200);
    }

    @Test
    void createBPMNOK() throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = new BpmnVersion();
        BpmnDTO bpmnDTO = new BpmnDTO();
        when(bpmnVersionMapper.toEntityCreation(any(BpmnCreationDto.class))).thenReturn(bpmnVersion);
        when(bpmnVersionService.createBPMN(any(BpmnVersion.class), any(File.class), any(String.class))).thenReturn(Uni.createFrom().item(bpmnVersion));
        when(bpmnVersionMapper.toDTO(bpmnVersion)).thenReturn(bpmnDTO);
        BpmnDTO result = given()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .multiPart("file", new File("src/test/resources/TestMalformed.bpmn"))
                .formParam("filename", "testFileName")
                .formParam("functionType", "MENU")
                .when().post("/api/v1/model/bpmn")
                .then()
                .statusCode(200)
                .extract().as(BpmnDTO.class);
        assertEquals(bpmnDTO, result);
    }

    @Test
    void deleteBpmn() {
        UUID bpmnId = UUID.randomUUID();
        Long version = 1L;
        BpmnVersionPK key = new BpmnVersionPK(bpmnId, version);
        when(bpmnVersionService.delete(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(true));
        given()
                .pathParam("bpmnId", bpmnId)
                .pathParam("version", version)
                .when().delete("/api/v1/model/bpmn/{bpmnId}/version/{version}")
                .then()
                .statusCode(204);
        verify(bpmnVersionService, times(1)).delete(key);
    }

    @Test
    void deployBPMN() {
        UUID bpmnId = UUID.randomUUID();
        Long version = 1L;
        BpmnVersionPK expectedKey = new BpmnVersionPK(bpmnId, version);
        BpmnVersion expectedBpmn = (new BpmnVersion());
        BpmnDTO expectedDto = new BpmnDTO();
        expectedDto.setBpmnId(bpmnId);
        expectedDto.setModelVersion(version);
        when(bpmnVersionService.deploy(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(expectedBpmn));
        when(bpmnVersionMapper.toDTO(any(BpmnVersion.class))).thenReturn(expectedDto);
        BpmnDTO result = given()
                .pathParam("uuid", bpmnId)
                .pathParam("version", version)
                .when().post("/api/v1/model/bpmn/deploy/{uuid}/version/{version}")
                .then()
                .statusCode(200)
                .extract().as(BpmnDTO.class);
        verify(bpmnVersionService, times(1)).deploy(expectedKey);
        verify(bpmnVersionMapper, times(1)).toDTO(expectedBpmn);
        assertEquals(bpmnId, result.getBpmnId());
        assertEquals(version, result.getModelVersion());
    }

//    @Test
//    void downloadBpmnOK() {
//        UUID bpmnId=UUID.randomUUID();
//        Long version=1L;
//        BpmnVersionPK expectedKey=new BpmnVersionPK(bpmnId,version);
//        BpmnVersion expectedBpmn=(new BpmnVersion());
//        expectedBpmn.setResourceFile(getResourceFileInstance());
//        //set storage key in resource file: that is the input
//        BpmnDTO expectedDto=new BpmnDTO();
//        expectedDto.setBpmnId(bpmnId);
//        expectedDto.setModelVersion(version);
//        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmn)));
//        when(bpmnFileStorageService.download(any(String.class))).thenReturn((RestMulti<Buffer>) buffer("downloaded"));
//
//        given()
//                .pathParam("uuid",bpmnId)
//                .pathParam("version",version)
//                .when().post("/api/v1/model/bpmn/deploy/{uuid}/version/{version}")
//                .then()
//                .statusCode(200);
//        verify(bpmnVersionService,times(1)).findByPk(expectedKey);
//        verify(bpmnFileStorageService,times(1)).download("storageKey");
//    }

    @Test
    void downloadBpmnNullResourceFile() {
        UUID bpmnId = UUID.randomUUID();
        Long version = 1L;
        BpmnVersionPK expectedKey = new BpmnVersionPK(bpmnId, version);
        BpmnVersion expectedBpmn = (new BpmnVersion());
        BpmnDTO expectedDto = new BpmnDTO();
        expectedDto.setBpmnId(bpmnId);
        expectedDto.setModelVersion(version);
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmn)));
        given()
                .pathParam("uuid", bpmnId)
                .pathParam("version", version)
                .when().get("/api/v1/model/bpmn/download/{uuid}/version/{version}")
                .then()
                .statusCode(500);
        verify(bpmnVersionService, times(1)).findByPk(expectedKey);
        verify(bpmnFileStorageService, times(0)).download(any(String.class));
    }

    @Test
    void downloadBpmnBlankStorageKey() {
        UUID bpmnId = UUID.randomUUID();
        Long version = 1L;
        BpmnVersionPK expectedKey = new BpmnVersionPK(bpmnId, version);
        ResourceFile expectedResourceFile = getResourceFileInstance();
        expectedResourceFile.setStorageKey("");
        BpmnVersion expectedBpmn = (new BpmnVersion());
        BpmnDTO expectedDto = new BpmnDTO();
        expectedDto.setBpmnId(bpmnId);
        expectedDto.setModelVersion(version);
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmn)));
        given()
                .pathParam("uuid", bpmnId)
                .pathParam("version", version)
                .when().get("/api/v1/model/bpmn/download/{uuid}/version/{version}")
                .then()
                .statusCode(500);
        verify(bpmnVersionService, times(1)).findByPk(expectedKey);
        verify(bpmnFileStorageService, times(0)).download(any(String.class));
    }

    @Test
    void downloadBpmnDoesNotExist() {
        UUID bpmnId = UUID.randomUUID();
        Long version = 1L;
        BpmnVersionPK expectedKey = new BpmnVersionPK(bpmnId, version);
        BpmnVersion expectedBpmn = (new BpmnVersion());
        expectedBpmn.setResourceFile(getResourceFileInstance());
        BpmnDTO expectedDto = new BpmnDTO();
        expectedDto.setBpmnId(bpmnId);
        expectedDto.setModelVersion(version);
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        given()
                .pathParam("uuid", bpmnId)
                .pathParam("version", version)
                .when().get("/api/v1/model/bpmn/download/{uuid}/version/{version}")
                .then()
                .statusCode(404);
        verify(bpmnVersionService, times(1)).findByPk(expectedKey);
        verify(bpmnFileStorageService, times(0)).download(any(String.class));
    }

    @Test
    void findBPMNByTriadThreeValues() {
        BpmnBankConfigPK expectedBankConfigPK = getBpmnBankConfigPKThreeValueInstance();
        BpmnBankConfig expectedBankConfig = new BpmnBankConfig();
        expectedBankConfig.setBpmnBankConfigPK(expectedBankConfigPK);
        BpmnVersionPK expectedBpmnVersionPK = new BpmnVersionPK(expectedBankConfigPK.getBpmnId(), expectedBankConfigPK.getBpmnModelVersion());
        BpmnVersion expectedBpmnVersion = new BpmnVersion();
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBankConfig)));
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmnVersion)));
        when(bpmnVersionMapper.toDTO(any(BpmnVersion.class))).thenReturn(new BpmnDTO());
        given()
                .pathParam("functionType", "MENU")
                .pathParam("acquirerId", "ACQ")
                .pathParam("branchId", "BRANCH")
                .pathParam("terminalId", "TERMINAL")
                .when().get("/api/v1/model/bpmn/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
                .then()
                .statusCode(200);
        verify(bpmnBankConfigService, times(1)).findByConfigurationsAndFunction("ACQ", "BRANCH", "TERMINAL", "MENU");
        verify(bpmnVersionService, times(1)).findByPk(expectedBpmnVersionPK);
    }

    @Test
    void findBPMNByTriadTwoValues() {
        BpmnBankConfigPK expectedBankConfigPK = getBpmnBankConfigPKThreeValueInstance();
        BpmnBankConfig expectedBankConfig = new BpmnBankConfig();
        expectedBankConfig.setBpmnBankConfigPK(expectedBankConfigPK);
        BpmnVersionPK expectedBpmnVersionPK = new BpmnVersionPK(expectedBankConfigPK.getBpmnId(), expectedBankConfigPK.getBpmnModelVersion());
        BpmnVersion expectedBpmnVersion = new BpmnVersion();
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), any(String.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBankConfig)));
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmnVersion)));
        when(bpmnVersionMapper.toDTO(any(BpmnVersion.class))).thenReturn(new BpmnDTO());
        given()
                .pathParam("functionType", "MENU")
                .pathParam("acquirerId", "ACQ")
                .pathParam("branchId", "BRANCH")
                .pathParam("terminalId", "NULL_VALUE")
                .when().get("/api/v1/model/bpmn/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
                .then()
                .statusCode(200);
        verify(bpmnBankConfigService, times(2)).findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(bpmnVersionService, times(1)).findByPk(expectedBpmnVersionPK);
        verify(bpmnVersionMapper, times(1)).toDTO(expectedBpmnVersion);
    }

    @Test
    void findBPMNByTriadOneValues() {
        BpmnBankConfigPK expectedBankConfigPK = getBpmnBankConfigPKThreeValueInstance();
        BpmnBankConfig expectedBankConfig = new BpmnBankConfig();
        expectedBankConfig.setBpmnBankConfigPK(expectedBankConfigPK);
        BpmnVersionPK expectedBpmnVersionPK = new BpmnVersionPK(expectedBankConfigPK.getBpmnId(), expectedBankConfigPK.getBpmnModelVersion());
        BpmnVersion expectedBpmnVersion = new BpmnVersion();
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), any(String.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBankConfig)));
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmnVersion)));
        when(bpmnVersionMapper.toDTO(any(BpmnVersion.class))).thenReturn(new BpmnDTO());
        given()
                .pathParam("functionType", "MENU")
                .pathParam("acquirerId", "ACQ")
                .pathParam("branchId", "NULL_VALUE")
                .pathParam("terminalId", "NULL_VALUE")
                .when().get("/api/v1/model/bpmn/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
                .then()
                .statusCode(200);
        verify(bpmnBankConfigService, times(3)).findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(bpmnVersionService, times(1)).findByPk(expectedBpmnVersionPK);
        verify(bpmnVersionMapper, times(1)).toDTO(expectedBpmnVersion);
    }

    @Test
    void findBPMNByTriadNotFoundException() {
        BpmnBankConfigPK expectedBankConfigPK = getBpmnBankConfigPKThreeValueInstance();
        BpmnBankConfig expectedBankConfig = new BpmnBankConfig();
        expectedBankConfig.setBpmnBankConfigPK(expectedBankConfigPK);
        BpmnVersionPK expectedBpmnVersionPK = new BpmnVersionPK(expectedBankConfigPK.getBpmnId(), expectedBankConfigPK.getBpmnModelVersion());
        BpmnVersion expectedBpmnVersion = new BpmnVersion();
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), any(String.class), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnBankConfigService.findByConfigurationsAndFunction(any(String.class), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), eq(BankConfigUtilityValues.NULL_VALUE.getValue()), any(String.class))).thenReturn(Uni.createFrom().item(Optional.empty()));
        when(bpmnVersionService.findByPk(any(BpmnVersionPK.class))).thenReturn(Uni.createFrom().item(Optional.of(expectedBpmnVersion)));
        when(bpmnVersionMapper.toDTO(any(BpmnVersion.class))).thenReturn(new BpmnDTO());
        given()
                .pathParam("functionType", "MENU")
                .pathParam("acquirerId", "NULL_VALUE")
                .pathParam("branchId", "NULL_VALUE")
                .pathParam("terminalId", "NULL_VALUE")
                .when().get("/api/v1/model/bpmn/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
                .then()
                .statusCode(400);
        verify(bpmnBankConfigService, times(3)).findByConfigurationsAndFunction(any(String.class), any(String.class), any(String.class), any(String.class));
        verify(bpmnVersionService, times(0)).findByPk(expectedBpmnVersionPK);
        verify(bpmnVersionMapper, times(0)).toDTO(expectedBpmnVersion);
    }

    @Test
    void upgradeBPMN() {
        BpmnUpgradeDto expectedUpmnUpgradeDto = new BpmnUpgradeDto();
        when(bpmnVersionService.upgrade(any(BpmnUpgradeDto.class))).thenReturn(Uni.createFrom().item(new BpmnDTO()));
        given()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .formParam("uuid", UUID.randomUUID())
                .multiPart("file", new File("src/test/resources/TestMalformed.bpmn"))
                .formParam("filename", "name")
                .formParam("functionType", "MENU")
                .when().post("/api/v1/model/bpmn/upgrade")
                .then()
                .statusCode(200);
        verify((bpmnVersionService), times(1)).upgrade(any(BpmnUpgradeDto.class));
    }

    @Test
    void getAssociations() {
        BpmnBankConfigDTO bpmnBankConfigDTO = new BpmnBankConfigDTO();
        List<BpmnBankConfigDTO> expectedList = new ArrayList<>();
        expectedList.add(bpmnBankConfigDTO);
        when(bpmnBankConfigService.findByAcquirerId(any(String.class))).thenReturn(Uni.createFrom().item(expectedList));
        given()
                .pathParam("acquirerId", "ACQ1")
                .when().get("/api/v1/model/bpmn/associations/bank/{acquirerId}")
                .then()
                .statusCode(200);
        verify(bpmnBankConfigService, times(1)).findByAcquirerId("ACQ1");
    }

    private static BpmnAssociationDto getBpmnAssociationDtoInstance() {
        List<String> terminalList = new ArrayList<>();
        terminalList.add("testTerminal");
        TerminalConfigs terminalConfigs = new TerminalConfigs(UUID.randomUUID(), 1L, terminalList);
        List<TerminalConfigs> terminals = new ArrayList<>();
        terminals.add(terminalConfigs);
        BranchConfigs branchConfigs = new BranchConfigs("testBranch", UUID.randomUUID(), 1L, terminals);
        List<BranchConfigs> branchesConfigs = new ArrayList<>();
        branchesConfigs.add(branchConfigs);
        return new BpmnAssociationDto(UUID.randomUUID(), 1L, branchesConfigs);
    }

    private static ResourceFile getResourceFileInstance() {
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setId(UUID.randomUUID());
        resourceFile.setResourceType(S3ResourceTypeEnum.BPMN);
        resourceFile.setStorageKey("storageKey");
        return resourceFile;
    }

    private static BpmnBankConfigPK getBpmnBankConfigPKThreeValueInstance() {
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(UUID.randomUUID());
        bpmnBankConfigPK.setBpmnModelVersion(1L);
        bpmnBankConfigPK.setAcquirerId("acquirer1");
        bpmnBankConfigPK.setBranchId("branch1");
        bpmnBankConfigPK.setTerminalId("terminal1");
        return bpmnBankConfigPK;
    }

    private static BpmnBankConfigPK getBpmnBankConfigPKTwoValueInstance() {
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(UUID.randomUUID());
        bpmnBankConfigPK.setBpmnModelVersion(1L);
        bpmnBankConfigPK.setAcquirerId("acquirer1");
        bpmnBankConfigPK.setBranchId("branch1");
        bpmnBankConfigPK.setTerminalId(BankConfigUtilityValues.NULL_VALUE.getValue());
        return bpmnBankConfigPK;
    }

    private static BpmnBankConfigPK getBpmnBankConfigPKOneValueInstance() {
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(UUID.randomUUID());
        bpmnBankConfigPK.setBpmnModelVersion(1L);
        bpmnBankConfigPK.setAcquirerId("acquirer1");
        bpmnBankConfigPK.setBranchId(BankConfigUtilityValues.NULL_VALUE.getValue());
        bpmnBankConfigPK.setTerminalId(BankConfigUtilityValues.NULL_VALUE.getValue());
        return bpmnBankConfigPK;
    }
}