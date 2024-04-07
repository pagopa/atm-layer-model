package it.gov.pagopa.atmlayer.service.model.resource;

import it.gov.pagopa.atmlayer.service.model.client.EngineClientTest;
import it.gov.pagopa.atmlayer.service.model.model.workflownotification.WorkflowNotification;
import it.gov.pagopa.atmlayer.service.model.service.pubsub.PubSubService;
import it.gov.pagopa.atmlayer.service.model.service.pubsub.SubscriptionPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
@Path("/workflow/listener")
@Tag(name = "Workflow Listener", description = "Workflow Listener APIs")
@Slf4j
public class WorkflowListenerResource {
    @Inject
    PubSubService pubSubService;

    @Inject
    @RestClient
    EngineClientTest engineClientTest;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/notify/{requestId}")
    public RestResponse<Void> callbackNotify(@PathParam("requestId") String requestId, @RequestBody WorkflowNotification workflowNotification) {
        this.pubSubService.notify(requestId, workflowNotification);
        return RestResponse.status(Response.Status.NO_CONTENT);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{requestId}")
    public WorkflowNotification subscribe(@PathParam("requestId") String requestId) {
        WorkflowNotification notification;
        SubscriptionPayload payload = this.pubSubService.subscribe(requestId);
        try {
            notification = payload.getFuture().get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            String errorMessage = String.format("No wait notification from engine for process with RequestId %s in the specified timeout", requestId);
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            payload.getSubscriber().unsubscribe(requestId);
        }
        return notification;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test/definition/{definitionId}/requestId/{requestId}")
    public WorkflowNotification test(@PathParam("definitionId") String definitionId, @PathParam("requestId") String requestId) throws InterruptedException {
        SubscriptionPayload subscriptionPayload = this.pubSubService.subscribe(requestId);
        Response response = engineClientTest.startProcessWithPayload(definitionId, prepareTestPayload(requestId));
        WorkflowNotification notification;
        try {
            notification = subscriptionPayload.getFuture().get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            String errorMessage = String.format("No wait notification from engine for process with RequestId %s in the specified timeout", requestId);
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscriptionPayload.getSubscriber().unsubscribe(requestId);
        }
        return notification;
    }


    private String prepareTestPayload(String requestId) {

        return "{\"businessKey\":null,\"variables\":{\"paTaxCode\":{\"value\":\"00000000201\",\"type\":\"String\"},\"noticeNumber\":{\"value\":\"012345678901234567\",\"type\":\"String\"},\"channel\":{\"value\":\"ATM\",\"type\":\"String\"},\"bankId\":{\"value\":\"06789\",\"type\":\"String\"},\"requestId\":{\"value\":\"" + requestId + "\",\"type\":\"String\"},\"terminalId\":{\"value\":\"64874412\",\"type\":\"String\"},\"authorization\":{\"value\":\"Bearer\",\"type\":\"String\"},\"contentType\":{\"value\":\"application/x-www-form-urlencoded\",\"type\":\"String\"},\"milCall\":{\"type\":\"boolean\",\"value\":false}}}";
    }


}
