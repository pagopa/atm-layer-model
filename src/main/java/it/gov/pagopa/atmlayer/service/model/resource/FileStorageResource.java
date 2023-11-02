
package it.gov.pagopa.atmlayer.service.model.resource;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import it.gov.pagopa.atmlayer.service.model.model.filestorage.FileObject;
import it.gov.pagopa.atmlayer.service.model.properties.ObjectStoreProperties;
import it.gov.pagopa.atmlayer.service.model.resource.filestorage.FileStorageCommonResource;
import it.gov.pagopa.atmlayer.service.model.service.BpmnFileStorageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import org.jboss.resteasy.reactive.RestMulti;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/async-s3")
@UnlessBuildProfile(anyOf = {"prod", "native"})
public class FileStorageResource extends FileStorageCommonResource {
    @Inject
    S3AsyncClient s3;
    @Inject
    ObjectStoreProperties objectStoreProperties;

    @Inject
    BpmnFileStorageService bpmnFileStorageService;


    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public RestMulti<Buffer> downloadFile(String objectKey) {

        return RestMulti.fromUniResponse(Uni.createFrom()
                        .completionStage(() -> s3.getObject(buildGetRequest(objectKey),
                                AsyncResponseTransformer.toPublisher())),
                response -> Multi.createFrom().safePublisher(AdaptersToFlow.publisher((Publisher<ByteBuffer>) response))
                        .map(this::toBuffer),
                response -> Map.of("Content-Disposition", List.of("attachment;filename=" + objectKey), "Content-Type",
                        List.of(response.response().contentType())));
    }

    public Buffer toBuffer(ByteBuffer bytebuffer) {
        byte[] result = new byte[bytebuffer.remaining()];
        bytebuffer.get(result);
        return Buffer.buffer(result);
    }

    @GET
    @Path("presigned-url/{objectKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<URL> getPresigned(String objectKey) {
        return bpmnFileStorageService.generatePresignedUrl(objectKey);
    }

    @GET
    public Uni<List<FileObject>> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .build();

        return Uni.createFrom().completionStage(() -> s3.listObjects(listRequest))
                .onItem().transform(result -> toFileItems(result));
    }

    private List<FileObject> toFileItems(ListObjectsResponse objects) {
        return objects.contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
    }
}
