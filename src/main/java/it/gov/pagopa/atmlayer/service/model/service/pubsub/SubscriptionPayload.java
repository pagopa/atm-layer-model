package it.gov.pagopa.atmlayer.service.model.service.pubsub;

import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import it.gov.pagopa.atmlayer.service.model.model.workflownotification.WorkflowNotification;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class SubscriptionPayload {
    private PubSubCommands.RedisSubscriber subscriber;
    private CompletableFuture<WorkflowNotification> future;
}
