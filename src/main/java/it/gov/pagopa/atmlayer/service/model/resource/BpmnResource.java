package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.gov.pagopa.atmlayer.service.model.client.ProcessClient;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.DeployResponseDto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import it.gov.pagopa.atmlayer.service.model.enumeration.FunctionTypeEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.service.impl.BpmnBankConfigService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnDtoMapper;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
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
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.getAcquirerConfigs;

@ApplicationScoped
@Path("/bpmn")
@Tag(name = "BPMN", description = "BPMN operations")
@Slf4j
public class BpmnResource {
    @Inject
    BpmnVersionService bpmnVersionService;

    @Inject
    BpmnBankConfigService bpmnBankConfigService;

    @Inject
    BpmnEntityValidator bpmnEntityValidator;
    @Inject
    @RestClient
    ProcessClient processClient;
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
    @Path("/bank/{acquirerId}/associations/function/{functionType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BpmnBankConfig>> associateBPMN(@PathParam("acquirerId") String acquirerId,
                                                   @PathParam("functionType") FunctionTypeEnum functionTypeEnum,
                                                   @RequestBody(required = true) @Valid BpmnAssociationDto bpmnAssociationDto) throws NoSuchAlgorithmException, IOException {
        List<BpmnBankConfig> configs = getAcquirerConfigs(bpmnAssociationDto, acquirerId, functionTypeEnum);
        Set<BpmnVersionPK> bpmnIds = BpmnUtils.extractBpmnUUIDFromAssociations(configs);
        return bpmnEntityValidator.validateExistenceAndStatus(bpmnIds)
                .onItem().transformToUni(x -> this.bpmnVersionService.putAssociations(acquirerId, functionTypeEnum, configs));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnVersion> createBPMN(@RequestBody(required = true) @Valid BpmnCreationDto bpmnCreationDto) throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = BpmnDtoMapper.toBpmnVersion(bpmnCreationDto);
        return bpmnVersionService.save(bpmnVersion);
    }

    @POST
    @Path("/deploy/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<DeployResponseDto> deployBPMN(@PathParam("uuid") UUID uuid,
                                             @PathParam("version") Long version) {
        bpmnVersionService.checkBpmnFileExistence(uuid, version)
                .onItem()
                .transformToUni(x -> {
                    if (!x) {
                        String errorMessage = "The referenced BPMN file can not be deployed";
                        throw new AtmLayerException(errorMessage, Response.Status.BAD_REQUEST, AppErrorCodeEnum.BPMN_FILE_CANNOT_BE_DEPLOYED);
                    }
                    return bpmnVersionService.setDeployInProgress(uuid, version);
                });
        return processClient.deploy("url");
    }

    @GET
    @Path("/function/{functionType}/bank{acquirerId}/branch/{branchId}/terminal/{terminalId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Optional<BpmnVersion>> findBPMNByTriad (@PathParam("functionType") FunctionTypeEnum functionTypeEnum,
                                             @PathParam("acquirerId") String acquirerId,
                                             @PathParam("branchId") String branchId,
                                             @PathParam("terminalId") String terminalId) {
        return bpmnBankConfigService.findByTriadAndFunction(acquirerId, branchId, terminalId, functionTypeEnum)
                .onItem()
                .transformToUni(x1 -> {
                    if (x1.isPresent()) {
                        return bpmnVersionService.findByPk(new BpmnVersionPK(x1.get().getBpmnBankConfigPK().getBpmnId(), x1.get().getBpmnBankConfigPK().getBpmnModelVersion()));
                    }
                    return bpmnBankConfigService.findByTriadAndFunction(acquirerId, branchId, BankConfigUtilityValues.NULL_VALUE.getValue(), functionTypeEnum)
                            .onItem()
                            .transformToUni(x2 -> {
                                if (x2.isPresent()) {
                                    return bpmnVersionService.findByPk(new BpmnVersionPK(x2.get().getBpmnBankConfigPK().getBpmnId(), x2.get().getBpmnBankConfigPK().getBpmnModelVersion()));
                                }
                                return Uni.createFrom().item(new BpmnVersion());
                            });
                });
    }
}
