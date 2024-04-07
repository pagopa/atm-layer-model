package it.gov.pagopa.atmlayer.service.model.service.pubsub;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import it.gov.pagopa.atmlayer.service.model.model.workflownotification.WorkflowNotification;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class PubSubService {

    private final PubSubCommands<WorkflowNotification> pubSubCommands;


    public PubSubService(RedisDataSource ds) {
        pubSubCommands = ds.pubsub(WorkflowNotification.class);
    }

    public void notify(String channel, WorkflowNotification workflowNotification) {
        pubSubCommands.publish(channel, workflowNotification);
    }

    public SubscriptionPayload subscribe(String channel) {
        CompletableFuture<WorkflowNotification> future = new CompletableFuture<>();
        PubSubCommands.RedisSubscriber subscriber = pubSubCommands.subscribe(channel, future::complete);
        return SubscriptionPayload.builder()
                .future(future)
                .subscriber(subscriber).build();
    }
}
