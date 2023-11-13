package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchConfigs;
import it.gov.pagopa.atmlayer.service.model.dto.TerminalConfigs;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapperImpl;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.ws.rs.core.MediaType;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
    BpmnConfigMapperImpl bpmnConfigMapper;

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
        Assertions.assertEquals(1, result.size());
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
        FunctionTypeEnum functionTypeEnum = FunctionTypeEnum.MENU;
        when(bpmnEntityValidator.validateExistenceStatusAndFunctionType(any(), any())).thenReturn(Uni.createFrom().nullItem());
        when(bpmnVersionService.putAssociations(any(), any(), any())).thenReturn(Uni.createFrom().item(bankConfigList));
        when(bpmnConfigMapper.toDTOList(bankConfigList)).thenReturn(dtoList);
        given()
                .pathParam("acquirerId", acquirerId)
                .pathParam("functionType", functionTypeEnum)
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
                .formParam("functionType", FunctionTypeEnum.MENU)
                .when().post("/api/v1/model/bpmn")
                .then()
                .statusCode(200)
                .extract().as(BpmnDTO.class);
        Assertions.assertEquals(bpmnDTO, result);
    }

    @Test
    void deleteBpmn() {
    }

    @Test
    void deployBPMN() {
    }

    @Test
    void downloadBpmn() {
    }

    @Test
    void findBPMNByTriad() {
    }

    @Test
    void upgradeBPMN() {
    }

    @Test
    void getAssociations() {
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
}