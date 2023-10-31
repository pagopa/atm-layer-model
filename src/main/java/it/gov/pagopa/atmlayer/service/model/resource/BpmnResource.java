package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfigPK;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.functionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnDtoMapper;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;

@ApplicationScoped
@Path("/bpmn")
@Tag(name = "BPMN", description = "BPMN operations")
@Slf4j
public class BpmnResource {


    @Inject
    BpmnVersionService bpmnVersionService;
    @Inject
    BpmnEntityValidator bpmnEntityValidator;
    @Inject
    BpmnFileStorageService bpmnFileStorageService;


//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getEncodedFile(@QueryParam("string") String s) throws IOException {
//        String xml = modelService.decodeBase64(s);
//        logger.info("String file: " + xml);
//        return "String file: " + xml;
//    }

    @GET
    @Path("/{bpmnId}/version/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnVersion> getEncodedFile(@PathParam("bpmnId") UUID bpmnId,
                                           @PathParam("version") Long version) {
        BpmnVersionPK key = BpmnVersionPK.builder()
                .bpmnId(bpmnId)
                .modelVersion(version)
                .build();
        return this.bpmnVersionService.findByPk(key)
                .onItem()
                .transform(Unchecked.function(x -> {
                    if (x.isEmpty()) {
                        throw new AtmLayerException(Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                    }
                    return x.get();
                }));
    }

    @PUT
    @Path("/bank/{acquirerId}/associations/function/{functionType}/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BpmnBankConfig>> associateBPMN(@PathParam("acquirerId") String acquirerId,
                                                   @PathParam("functionType") functionTypeEnum functionTypeEnum,
                                                   @PathParam("id") UUID id,
                                                   @RequestBody(required = true) @Valid BpmnAssociationDto bpmnAssociationDto) throws NoSuchAlgorithmException, IOException {

        // TO DO, metodo che dal DTO estrae la lista di configs
        List<BpmnBankConfig> configs = extractConfigs(acquirerId, functionTypeEnum, id);
        Set<BpmnVersionPK> bpmnIds = BpmnUtils.extractBpmnUUIDFromAssociations(configs);
        return bpmnEntityValidator.validateExistence(bpmnIds)
                .onItem().transformToUni(x -> this.bpmnVersionService.putAssociations(acquirerId, functionTypeEnum, configs));
    }

    private static List<BpmnBankConfig> extractConfigs(String acquirerId, functionTypeEnum functionTypeEnum, UUID id) {
        BpmnBankConfigPK bpmnBankConfigPK = new BpmnBankConfigPK();
        bpmnBankConfigPK.setBpmnId(id);
        bpmnBankConfigPK.setBpmnModelVersion(1L);
        bpmnBankConfigPK.setAcquirerId(acquirerId);
        bpmnBankConfigPK.setBranchId("1");
        bpmnBankConfigPK.setTerminalId("1");
        BpmnBankConfigPK bpmnBankConfigPK2 = new BpmnBankConfigPK();
        bpmnBankConfigPK2.setBpmnId(id);
        bpmnBankConfigPK2.setBpmnModelVersion(1L);
        bpmnBankConfigPK2.setAcquirerId(acquirerId);
        bpmnBankConfigPK2.setBranchId("1");
        bpmnBankConfigPK2.setTerminalId("2");
        BpmnBankConfig bpmnBankConfig = new BpmnBankConfig();
        BpmnBankConfig bpmnBankConfig1 = new BpmnBankConfig();
        bpmnBankConfig.setBpmnBankConfigPK(bpmnBankConfigPK);
        bpmnBankConfig.setFunctionType(functionTypeEnum);
        bpmnBankConfig1.setBpmnBankConfigPK(bpmnBankConfigPK2);
        bpmnBankConfig1.setFunctionType(functionTypeEnum);
        return List.of(bpmnBankConfig, bpmnBankConfig1);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<BpmnVersion> createBPMN(@RequestBody(required = true) @Valid BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = BpmnDtoMapper.toBpmnVersion(bpmnCreationDto);
        return bpmnVersionService.saveAndUpload(bpmnVersion, bpmnCreationDto.getFile(), bpmnCreationDto.getFilename());
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{bpmnId}/version/{version}")
    public Uni<Void> deleteBpmn(@PathParam("bpmnId") UUID bpmnId,
                                @PathParam("version") Long version) {

        return this.bpmnVersionService.delete(new BpmnVersionPK(bpmnId, version))
                .onItem().ignore().andSwitchTo(Uni.createFrom().voidItem());
    }
}
