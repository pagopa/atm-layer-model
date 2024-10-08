package it.gov.pagopa.atmlayer.service.model.resource;

import io.opentelemetry.api.trace.Tracer;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.dto.*;
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
import it.gov.pagopa.atmlayer.service.model.model.*;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import it.gov.pagopa.atmlayer.service.model.service.BpmnVersionService;
import it.gov.pagopa.atmlayer.service.model.service.impl.BpmnBankConfigService;
import it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils;
import it.gov.pagopa.atmlayer.service.model.validators.BpmnEntityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorCodeEnum.BPMN_FILE_DOES_NOT_EXIST;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.getAcquirerConfigs;
import static it.gov.pagopa.atmlayer.service.model.utils.BpmnUtils.validateBankConfigTriplet;

@ApplicationScoped
@Path("/bpmn")
@Tag(name = "BPMN", description = "BPMN operations")
@Slf4j
public class BpmnResource {

    private final BpmnVersionService bpmnVersionService;
    private final BpmnBankConfigService bpmnBankConfigService;
    private final BpmnEntityValidator bpmnEntityValidator;
    private final BpmnFileStorageService bpmnFileStorageService;
    private final BpmnVersionMapper bpmnVersionMapper;
    private final BpmnConfigMapper bpmnConfigMapper;

    @Inject
    public BpmnResource(BpmnVersionService bpmnVersionService, BpmnBankConfigService bpmnBankConfigService,
                        BpmnEntityValidator bpmnEntityValidator, BpmnFileStorageService bpmnFileStorageService,
                        BpmnVersionMapper bpmnVersionMapper, BpmnConfigMapper bpmnConfigMapper) {
        this.bpmnVersionService = bpmnVersionService;
        this.bpmnBankConfigService = bpmnBankConfigService;
        this.bpmnEntityValidator = bpmnEntityValidator;
        this.bpmnFileStorageService = bpmnFileStorageService;
        this.bpmnVersionMapper = bpmnVersionMapper;
        this.bpmnConfigMapper = bpmnConfigMapper;
    }

    @GET
    @Path("/{bpmnId}/version/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "getEncodedFile",
            description = "Recupera il file BPMN codificato"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnDTO> getEncodedFile(@PathParam("bpmnId") UUID bpmnId,
                                       @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
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
    @Operation(
            operationId = "associateBPMN",
            description = "Associa BPMN a un acquirer specifico"
    )
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public @Schema(maxItems = 50000) Uni<Collection<BpmnBankConfigDTO>> associateBPMN(
            @PathParam("acquirerId") @Schema(format = "byte", maxLength = 255) String acquirerId,
            @PathParam("functionType") @Schema(format = "byte", maxLength = 255) String functionType,
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
    @Operation(
            operationId = "createBPMN",
            description = "Crea un nuovo BPMN"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
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
    @Operation(
            operationId = "deleteBpmn",
            description = "Elimina BPMN"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> deleteBpmn(@PathParam("bpmnId") UUID bpmnId,
                                @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
        return this.bpmnVersionService.delete(new BpmnVersionPK(bpmnId, version))
                .onItem()
                .ignore()
                .andSwitchTo(Uni.createFrom().voidItem());
    }

    @POST
    @Path("/deploy/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "deployBpmn",
            description = "Rilascio BPMN"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnDTO> deployBPMN(@PathParam("uuid") UUID uuid,
                                   @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
        return this.bpmnVersionService.deploy(new BpmnVersionPK(uuid, version))
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().item(this.bpmnVersionMapper.toDTO(bpmn)));
    }

    @POST
    @Path("/undeploy/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "undeployBpmn",
            description = "Disattiva il rilascio di un file BPMN specifico"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> undeployBPMN(@PathParam("uuid") UUID uuid) {
        return this.bpmnVersionService.undeploy(uuid)
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().voidItem());
    }

    @GET
    @Path("/download/{uuid}/version/{version}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(
            operationId = "downloadBpmn",
            description = "Scarica file BPMN"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = Buffer.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Multi<Buffer> downloadBpmn(@PathParam("uuid") UUID bpmnId,
                                      @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
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
    @Operation(
            operationId = "downloadBpmnFrontEnd",
            description = "Scarica il file BPMN dal front-end"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = FileS3Dto.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<FileS3Dto> downloadBpmnFrontEnd(@PathParam("uuid") UUID bpmnId,
                                               @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
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
    @Operation(
            operationId = "findBPMNByTriad",
            description = "Cerca file BPMN per tripletta: acquirerId, branchId e terminalId"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnDTO> findBPMNByTriad(@PathParam("functionType") @Schema(format = "byte", maxLength = 255) String functionType,
                                        @PathParam("acquirerId") @Schema(format = "byte", maxLength = 255) String acquirerId,
                                        @PathParam("branchId") @Schema(format = "byte", maxLength = 255) String branchId,
                                        @PathParam("terminalId") @Schema(format = "byte", maxLength = 255) String terminalId) {
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
    @Operation(
            operationId = "findBPMNByTriadForProcessService",
            description = "Cerca file BPMN per tripletta acquirerId, branchId e terminalId per Process Service"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnProcessDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnProcessDTO> findBPMNByTriadForProcessService(
            @PathParam("functionType") @Schema(format = "byte", maxLength = 255) String functionType,
            @PathParam("acquirerId") @Schema(format = "byte", maxLength = 255) String acquirerId,
            @PathParam("branchId") @Schema(format = "byte", maxLength = 255) String branchId,
            @PathParam("terminalId") @Schema(format = "byte", maxLength = 255) String terminalId) {
        return this.findBPMNByTriad(functionType, acquirerId, branchId, terminalId)
                .onItem()
                .transformToUni(bpmn -> Uni.createFrom().item(bpmnVersionMapper.toProcessDTO(bpmn)));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/upgrade")
    @Operation(
            operationId = "upgradeBPMN",
            description = "Aggiorna il file BPMN aumentando la versione"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnDTO> upgradeBPMN(@Valid BpmnUpgradeDto bpmnUpgradeDto) {
        bpmnUpgradeDto.setFunctionType(bpmnUpgradeDto.getFunctionType().toUpperCase());
        return bpmnVersionService.upgrade(bpmnUpgradeDto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/bank/{acquirerId}")
    @Operation(
            operationId = "getAssociations",
            description = "cerca associazioni di un acquirerId"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = BpmnBankConfigDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<List<BpmnBankConfigDTO>> getAssociations(@PathParam("acquirerId") @Schema(format = "byte", maxLength = 255) String acquirerId) {
        return bpmnBankConfigService.findByAcquirerId(acquirerId);
    }

    @POST
    @Path("/disable/{uuid}/version/{version}")
    @Operation(
            operationId = "disableBPMN",
            description = "disabilita un file BPMN"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> disableBPMN(@PathParam("uuid") UUID bpmnId, @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version) {
        BpmnVersionPK bpmnVersionPK = new BpmnVersionPK(bpmnId, version);
        return bpmnVersionService.disable(bpmnVersionPK);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/filter")
    @Operation(
            operationId = "getBpmnFiltered",
            description = "cerca BPMN mettendo dei filtri"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = PageInfo.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<PageInfo<BpmnFrontEndDTO>> getBpmnFiltered(@QueryParam("pageIndex") @DefaultValue("0")
                                                          @Parameter(required = true, schema = @Schema(minimum = "0", maximum = "10000")) int pageIndex,
                                                          @QueryParam("pageSize") @DefaultValue("10")
                                                          @Parameter(required = true, schema = @Schema(minimum = "1", maximum = "100")) int pageSize,
                                                          @QueryParam("functionType") @Schema(format = "byte", maxLength = 255) String functionType,
                                                          @QueryParam("modelVersion") @Schema(format = "byte", maxLength = 5) String modelVersion,
                                                          @QueryParam("definitionVersionCamunda") @Schema(format = "byte", maxLength = 5) String definitionVersionCamunda,
                                                          @QueryParam("bpmnId") UUID bpmnId,
                                                          @QueryParam("deploymentId") UUID deploymentId,
                                                          @QueryParam("camundaDefinitionId") @Schema(format = "byte", maxLength = 255) String camundaDefinitionId,
                                                          @QueryParam("definitionKey") @Schema(format = "byte", maxLength = 255) String definitionKey,
                                                          @QueryParam("deployedFileName") @Schema(format = "byte", maxLength = 255) String deployedFileName,
                                                          @QueryParam("resource") @Schema(format = "byte", maxLength = 255) String resource,
                                                          @QueryParam("sha256") @Schema(format = "byte", maxLength = 255) String sha256,
                                                          @QueryParam("status") StatusEnum status,
                                                          @QueryParam("acquirerId") @Schema(format = "byte", maxLength = 255) String acquirerId,
                                                          @QueryParam("branchId") @Schema(format = "byte", maxLength = 255) String branchId,
                                                          @QueryParam("terminalId") @Schema(format = "byte", maxLength = 255) String terminalId,
                                                          @QueryParam("fileName") @Schema(format = "byte", maxLength = 255) String fileName) {
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
    @Operation(
            operationId = "getAssociationsByBpmn",
            description = "cerca associazioni di un BPMN"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = PageInfo.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<PageInfo<BpmnBankConfigDTO>> getAssociationsByBpmn(@PathParam("uuid") UUID bpmnId, @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version,
                                                                  @QueryParam("pageIndex") @DefaultValue("0") @Schema(minimum = "0", maximum = "10000") int pageIndex,
                                                                  @QueryParam("pageSize") @DefaultValue("10") @Schema(minimum = "1", maximum = "100") int pageSize) {
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
    @Operation(
            operationId = "addSingleAssociation",
            description = "aggiungi una singola associazione"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnBankConfigDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnBankConfigDTO> addSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version,
                                                       @RequestBody(required = true) BankConfigTripletDto bankConfigTripletDto) {
        validateBankConfigTriplet(bankConfigTripletDto);
        return bpmnVersionService.addSingleAssociation(new BpmnVersionPK(bpmnId, version), bankConfigTripletDto)
                .onItem().transformToUni(newBankConfig -> Uni.createFrom().item(bpmnConfigMapper.toDTO(newBankConfig)));
    }

    @DELETE
    @Path("/associations/{uuid}/version/{version}")
    @Operation(
            operationId = "deleteSingleAssociation",
            description = "elimina una singola associazione"
    )
    @APIResponse(responseCode = "204", description = "Ok")
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<Void> deleteSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version,
                                             @QueryParam("acquirerId") @NotEmpty @Schema(format = "byte", maxLength = 255) String acquirerId,
                                             @QueryParam("branchId") @Schema(format = "byte", maxLength = 255) String branchId,
                                             @QueryParam("terminalId") @Schema(format = "byte", maxLength = 255) String terminalId) {
        validateBankConfigTriplet(new BankConfigTripletDto(acquirerId, branchId, terminalId));
        BankConfigDeleteDto bankConfigDeleteDto = new BankConfigDeleteDto(bpmnId, version, acquirerId, branchId, terminalId);
        return bpmnVersionService.deleteSingleAssociation(bankConfigDeleteDto);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/associations/{uuid}/version/{version}")
    @Operation(
            operationId = "replaceSingleAssociation",
            description = "sostituisci una singola associazione"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BpmnBankConfigDTO.class)))
    @APIResponse(responseCode = "4XX", description = "Bad Request", content = @Content(example = "{\"type\":\"BAD_REQUEST\", \"statusCode\":\"4XX\", \"message\":\"Messaggio di errore\", \"errorCode\":\"ATMLM_4000XXX\"}"))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(example = "{\"type\":\"GENERIC\", \"statusCode\":\"500\", \"message\":\"Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni\", \"errorCode\":\"ATMLM_500\"}"))
    public Uni<BpmnBankConfigDTO> replaceSingleAssociation(@PathParam("uuid") UUID bpmnId, @PathParam("version") @Schema(minimum = "1", maximum = "10000") Long version,
                                                           @RequestBody(required = true) BankConfigTripletDto bankConfigTripletDto) {
        validateBankConfigTriplet(bankConfigTripletDto);
        return bpmnVersionService.replaceSingleAssociation(new BpmnVersionPK(bpmnId, version), bankConfigTripletDto)
                .onItem().transformToUni(newBankConfig -> Uni.createFrom().item(bpmnConfigMapper.toDTO(newBankConfig)));
    }
}
