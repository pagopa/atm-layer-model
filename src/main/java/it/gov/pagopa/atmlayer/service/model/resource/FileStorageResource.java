package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersionPK;
import it.gov.pagopa.atmlayer.service.model.model.BpmnIdDto;
import it.gov.pagopa.atmlayer.service.model.model.filestorage.FileObject;
import it.gov.pagopa.atmlayer.service.model.model.filestorage.FormData;
import it.gov.pagopa.atmlayer.service.model.resource.filestorage.FileStorageCommonResource;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import org.jboss.resteasy.reactive.RestMulti;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/async-s3")
public class FileStorageResource extends FileStorageCommonResource {
    @Inject
    S3AsyncClient s3;

    @Inject
    BpmnFileStorageService bpmnFileStorageService;

    @GET
    @Path("type")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Void> getTye() {
        bpmnFileStorageService.uploadFile(new BpmnIdDto(UUID.randomUUID(), 1L), new File("prova"), "prova");

        return Uni.createFrom().nullItem();
    }

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> uploadFile(@Valid FormData formData) throws Exception {

        return bpmnFileStorageService.uploadFile(new BpmnIdDto(UUID.randomUUID(), 1L), formData.data, formData.filename)
                .onItem().ignore().andSwitchTo(Uni.createFrom().item(Response.created(null).build()));
    }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public RestMulti<Buffer> downloadFile(String objectKey) {

        return RestMulti.fromUniResponse(Uni.createFrom()
                        .completionStage(() -> s3.getObject(buildGetRequest(objectKey),
                                AsyncResponseTransformer.toPublisher())),
                response -> Multi.createFrom().safePublisher(AdaptersToFlow.publisher((Publisher<ByteBuffer>) response))
                        .map(FileStorageResource::toBuffer),
                response -> Map.of("Content-Disposition", List.of("attachment;filename=" + objectKey), "Content-Type",
                        List.of(response.response().contentType())));
    }

    @GET
    public Uni<List<FileObject>> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        return Uni.createFrom().completionStage(() -> s3.listObjects(listRequest))
                .onItem().transform(result -> toFileItems(result));
    }

    private static Buffer toBuffer(ByteBuffer bytebuffer) {
        byte[] result = new byte[bytebuffer.remaining()];
        bytebuffer.get(result);
        return Buffer.buffer(result);
    }

    private List<FileObject> toFileItems(ListObjectsResponse objects) {
        return objects.contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
    }
}