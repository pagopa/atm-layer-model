package it.gov.pagopa.atmlayer.service.model.resource;
//import io.opentelemetry.api.trace.Tracer;

import io.opentelemetry.api.trace.Tracer;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.BankConfigDeleteDto;
import it.gov.pagopa.atmlayer.service.model.dto.BankConfigTripletDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnAssociationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnCreationDto;
import it.gov.pagopa.atmlayer.service.model.dto.BpmnUpgradeDto;
import it.gov.pagopa.atmlayer.service.model.dto.FileS3Dto;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnBankConfig;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum;
import it.gov.pagopa.atmlayer.service.model.enumeration.BankConfigUtilityValues;
import it.gov.pagopa.atmlayer.service.model.enumeration.StatusEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapper;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnVersionMapper;
import it.gov.pagopa.atmlayer.service.model.model.BpmnBankConfigDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.model.BpmnProcessDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.service.impl.BpmnBankConfigService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestMulti;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.getAcquirerConfigs;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.validateBankConfigTriplet;

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
    BpmnFileStorageService bpmnFileStorageService;
    @Inject
    BpmnVersionMapper bpmnVersionMapper;
    @Inject
    BpmnConfigMapper bpmnConfigMapper;
    @Inject
    Tracer tracer;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BpmnDTO>> getAllBpmn() {
        return this.bpmnVersionService.getAll()
                .onItem()
                .transform(Unchecked.function(list -> {
                    if (list.isEmpty()) {
                        log.info("No BPMN files saved in database");
                    }
                    return bpmnVersionMapper.toDTOList(list);
                }));
    }

    @GET
    @Path("/{bpmnId}/version/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnDTO> getEncodedFile(@PathParam("bpmnId") UUID bpmnId,
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
                    return bpmnVersionMapper.toDTO(x.get());
                }));
    }

    @PUT
    @Path("/bank/{acquirerId}/associations/function/{functionType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Collection<BpmnBankConfigDTO>> associateBPMN(
            @PathParam("acquirerId") String acquirerId,
            @PathParam("functionType") String functionType,
            @RequestBody(required = true) @Valid BpmnAssociationDto bpmnAssociationDto) {
        List<BpmnBankConfig> configs = getAcquirerConfigs(bpmnAssociationDto, acquirerId,
                functionType.toUpperCase());
        Set<BpmnVersionPK> bpmnIds = BpmnUtils.extractBpmnUUIDFromAssociations(configs);
        return bpmnEntityValidator.validateExistenceStatusAndFunctionType(bpmnIds, functionType.toUpperCase())
                .onItem()
                .transformToUni(
                        x -> this.bpmnVersionService.putAssociations(acquirerId, functionType.toUpperCase(), configs))
                .onItem()
                .transformToUni(list -> Uni.createFrom().item(this.bpmnConfigMapper.toDTOList(list)));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
    public Uni<BpmnDTO> createBPMN(
            @RequestBody(required = true) @Valid BpmnCreationDto bpmnCreationDto)
            throws NoSuchAlgorithmException, IOException {
        BpmnVersion bpmnVersion = bpmnVersionMapper.toEntityCreation(bpmnCreationDto);
        return this.bpmnVersionService.createBPMN(bpmnVersion, bpmnCreationDto.getFile(),
                        bpmnCreationDto.getFilename())
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().item(this.bpmnVersionMapper.toDTO(bpmn)));
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{bpmnId}/version/{version}")
    public Uni<Void> deleteBpmn(@PathParam("bpmnId") UUID bpmnId,
                                @PathParam("version") Long version) {
        return this.bpmnVersionService.delete(new BpmnVersionPK(bpmnId, version))
                .onItem()
                .ignore()
                .andSwitchTo(Uni.createFrom().voidItem());
    }

    @POST
    @Path("/deploy/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnDTO> deployBPMN(@PathParam("uuid") UUID uuid,
                                   @PathParam("version") Long version) {
        return this.bpmnVersionService.deploy(new BpmnVersionPK(uuid, version))
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().item(this.bpmnVersionMapper.toDTO(bpmn)));
    }

    @GET
    @Path("/download/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Multi<Buffer> downloadBpmn(@PathParam("uuid") UUID bpmnId,
                                      @PathParam("version") Long version) {
        BpmnVersionPK key = BpmnVersionPK.builder()
                .bpmnId(bpmnId)
                .modelVersion(version)
                .build();
        return this.bpmnVersionService.findByPk(key)
                .onItem().transformToMulti(
                        Unchecked.function(bpmn -> {
                            if (bpmn.isEmpty()) {
                                throw new AtmLayerException(Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                            }
                            ResourceFile resourceFile = bpmn.get().getResourceFile();
                            if (Objects.isNull(resourceFile) || StringUtils.isBlank(
                                    resourceFile.getStorageKey())) {
                                String errorMessage = String.format(
                                        "Nessun file associato a BPMN o nessuna chiave di archiviazione trovata: %s", key);
                                log.error(errorMessage);
                                throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR,
                                        AppErrorCodeEnum.BPMN_INTERNAL_ERROR);
                            }
                            return this.bpmnFileStorageService.download(resourceFile.getStorageKey());
                        }));
    }

    @GET
    @Path("/downloadFrontEnd/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<FileS3Dto> downloadBpmnFrontEnd(@PathParam("uuid") UUID bpmnId,
                                               @PathParam("version") Long version) {
        BpmnVersionPK key = BpmnVersionPK.builder()
                .bpmnId(bpmnId)
                .modelVersion(version)
                .build();
        return this.bpmnVersionService.findByPk(key)
                .onItem().transformToUni(
                        Unchecked.function(bpmn -> {
                            if (bpmn.isEmpty()) {
                                throw new AtmLayerException(Response.Status.NOT_FOUND, BPMN_FILE_DOES_NOT_EXIST);
                            }
                            ResourceFile resourceFile = bpmn.get().getResourceFile();
                            if (Objects.isNull(resourceFile) || StringUtils.isBlank(
                                    resourceFile.getStorageKey())) {
                                String errorMessage = String.format(
                                        "Nessun file associato a BPMN o nessuna chiave di archiviazione trovata: %s", key);
                                log.error(errorMessage);
                                throw new AtmLayerException(errorMessage, Response.Status.INTERNAL_SERVER_ERROR,
                                        AppErrorCodeEnum.BPMN_INTERNAL_ERROR);
                            }
                            return this.bpmnFileStorageService.downloadForFrontEnd(resourceFile.getStorageKey());
                        }));
    }

    @GET
    @Path("/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnDTO> findBPMNByTriad(@PathParam("functionType") String functionType,
                                        @PathParam("acquirerId") String acquirerId,
                                        @PathParam("branchId") String branchId,
                                        @PathParam("terminalId") String terminalId) {
        return bpmnBankConfigService.findByConfigurationsAndFunction(acquirerId, branchId, terminalId,
                        functionType)
                .onItem()
                .transformToUni(x1 -> {
                    if (x1.isPresent()) {
                        return bpmnVersionService.findByPk(
                                        new BpmnVersionPK(x1.get().getBpmnBankConfigPK().getBpmnId(),
                                                x1.get().getBpmnBankConfigPK().getBpmnModelVersion()))
                                .onItem()
                                .transformToUni(
                                        bpmn1 -> Uni.createFrom().item(this.bpmnVersionMapper.toDTO(bpmn1.get())));
                    }
                    return bpmnBankConfigService.findByConfigurationsAndFunction(acquirerId, branchId,
                                    BankConfigUtilityValues.NULL_VALUE.getValue(), functionType)
                            .onItem()
                            .transformToUni(x2 -> {
                                if (x2.isPresent()) {
                                    return bpmnVersionService.findByPk(
                                                    new BpmnVersionPK(x2.get().getBpmnBankConfigPK().getBpmnId(),
                                                            x2.get().getBpmnBankConfigPK().getBpmnModelVersion()))
                                            .onItem().transformToUni(bpmn2 -> Uni.createFrom()
                                                    .item(this.bpmnVersionMapper.toDTO(bpmn2.get())));
                                }
                                return bpmnBankConfigService.findByConfigurationsAndFunction(acquirerId,
                                                BankConfigUtilityValues.NULL_VALUE.getValue(),
                                                BankConfigUtilityValues.NULL_VALUE.getValue(), functionType)
                                        .onItem().transformToUni(Unchecked.function(x3 -> {
                                            if (x3.isPresent()) {
                                                return bpmnVersionService.findByPk(
                                                                new BpmnVersionPK(x3.get().getBpmnBankConfigPK().getBpmnId(),
                                                                        x3.get().getBpmnBankConfigPK().getBpmnModelVersion()))
                                                        .onItem()
                                                        .transformToUni(bpmn3 -> Uni.createFrom()
                                                                .item(this.bpmnVersionMapper.toDTO(bpmn3.get())));
                                            }
                                            throw new AtmLayerException("Nessun BPMN eseguibile trovato per la selezione",
                                                    Response.Status.BAD_REQUEST,
                                                    AppErrorCodeEnum.NO_BPMN_FOUND_FOR_CONFIGURATION);
                                        }));
                            });
                });
    }

    @GET
    @Path("/process/function/{functionType}/bank/{acquirerId}/branch/{branchId}/terminal/{terminalId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BpmnProcessDTO> findBPMNByTriadForProcessService(
            @PathParam("functionType") String functionType,
            @PathParam("acquirerId") String acquirerId,
            @PathParam("branchId") String branchId,
            @PathParam("terminalId") String terminalId) {
        return this.findBPMNByTriad(functionType, acquirerId, branchId, terminalId)
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().item(bpmnVersionMapper.toProcessDTO(bpmn)));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/upgrade")
    public Uni<BpmnDTO> upgradeBPMN(@Valid BpmnUpgradeDto bpmnUpgradeDto) {
        bpmnUpgradeDto.setFunctionType(bpmnUpgradeDto.getFunctionType().toUpperCase());
        return bpmnVersionService.upgrade(bpmnUpgradeDto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/bank/{acquirerId}")
    public Uni<List<BpmnBankConfigDTO>> getAssociations(@PathParam("acquirerId") String acquirerId) {
        return bpmnBankConfigService.findByAcquirerId(acquirerId);
    }

    @POST
    @Path("/disable/{uuid}/version/{version}")
    public Uni<Void> disableBPMN(@PathParam("uuid") UUID bpmnId, @PathParam("version") Long version) {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(bpmnId, version);
        return bpmnVersionService.disable(bpmnVersionPK);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/filter")
    public Uni<PageInfo<BpmnFrontEndDTO>> getBpmnFiltered(@QueryParam("pageIndex") @DefaultValue("0")
                                                          @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "0")) int pageIndex,
                                                          @QueryParam("pageSize") @DefaultValue("10")
                                                          @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "1")) int pageSize,
                                                          @QueryParam("functionType") String functionType,
                                                          @QueryParam("modelVersion") String modelVersion,
                                                          @QueryParam("definitionVersionCamunda") String definitionVersionCamunda,
                                                          @QueryParam("bpmnId") UUID bpmnId,
                                                          @QueryParam("deploymentId") UUID deploymentId,
                                                          @QueryParam("camundaDefinitionId") String camundaDefinitionId,
                                                          @QueryParam("definitionKey") String definitionKey,
                                                          @QueryParam("deployedFileName") String deployedFileName,
                                                          @QueryParam("resource") String resource,
                                                          @QueryParam("sha256") String sha256,
                                                          @QueryParam("status") StatusEnum status,
                                                          @QueryParam("acquirerId") String acquirerId,
                                                          @QueryParam("branchId") String branchId,
                                                          @QueryParam("terminalId") String terminalId,
                                                          @QueryParam("fileName") String fileName) {
        return bpmnVersionService.findBpmnFiltered(pageIndex, pageSize, functionType, modelVersion, definitionVersionCamunda,
                        bpmnId, deploymentId, camundaDefinitionId, definitionKey, deployedFileName, resource, sha256, status, acquirerId, branchId, terminalId, fileName)
                .onItem()
                .transform(Unchecked.function(pagedList -> {
                    if (pagedList.getResults().isEmpty()) {
                        log.info("No Bpmn file meets the applied filters");
                    }
                    return bpmnVersionMapper.toFrontEndDTOListPaged(pagedList);
                }));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/{uuid}/version/{version}")
    public Uni<PageInfo<BpmnBankConfigDTO>> getAssociationsByBpmn(@PathParam("uuid") UUID bpmnId, @PathParam("version") Long version,
                                                                  @QueryParam("pageIndex") @DefaultValue("0") @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "0")) int pageIndex,
                                                                  @QueryParam("pageSize") @DefaultValue("10") @Parameter(required = true, schema = @Schema(type = SchemaType.INTEGER, minimum = "1")) int pageSize) {
        return bpmnBankConfigService.findByBpmnPKPaged(new BpmnVersionPK(bpmnId, version), pageIndex, pageSize)
                .onItem()
                .transformToUni(pagedAssociations -> {
                    if (pagedAssociations.getResults().isEmpty()) {
                        log.info("No associations found for BpmnInd= {} and modelVersion= {}", bpmnId, version);
                    }
                    return Uni.createFrom().item(bpmnConfigMapper.toDTOListPaged(pagedAssociations));
                });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/{uuid}/version/{version}")
    public Uni<BpmnBankConfigDTO> addSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") Long version,
                                                       @RequestBody(required = true) BankConfigTripletDto bankConfigTripletDto) {
        validateBankConfigTriplet(bankConfigTripletDto);
        return bpmnVersionService.addSingleAssociation(new BpmnVersionPK(bpmnId, version), bankConfigTripletDto)
                .onItem().transformToUni(newBankConfig -> Uni.createFrom().item(bpmnConfigMapper.toDTO(newBankConfig)));
    }

    @DELETE
    @Path("/associations/{uuid}/version/{version}")
    public Uni<Void> deleteSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") Long version,
                                             @QueryParam("acquirerId") @NotEmpty String acquirerId,
                                             @QueryParam("branchId") String branchId,
                                             @QueryParam("terminalId") String terminalId) {
        validateBankConfigTriplet(new BankConfigTripletDto(acquirerId, branchId, terminalId));
        BankConfigDeleteDto bankConfigDeleteDto = new BankConfigDeleteDto(bpmnId, version, acquirerId, branchId, terminalId);
        return bpmnVersionService.deleteSingleAssociation(bankConfigDeleteDto);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/{uuid}/version/{version}")
    public Uni<BpmnBankConfigDTO> replaceSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") Long version,
                                                           @RequestBody(required = true) BankConfigTripletDto bankConfigTripletDto) {
        validateBankConfigTriplet(bankConfigTripletDto);
        return bpmnVersionService.replaceSingleAssociation(new BpmnVersionPK(bpmnId, version), bankConfigTripletDto)
                .onItem().transformToUni(newBankConfig -> Uni.createFrom().item(bpmnConfigMapper.toDTO(newBankConfig)));
    }
}
