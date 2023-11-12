package it.gov.pagopa.atmlayer.service.model.integrationtests;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@QuarkusTest
public class EnviromentTestServicesResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnviromentTestServicesResource.class);

    public static class DockerCompose implements QuarkusTestResourceLifecycleManager {
        private DockerComposeContainer<?> dockerComposeContainer;

        @Override
        public Map<String, String> start() {

            dockerComposeContainer = new DockerComposeContainer<>(new File("src/test/resources/integration-test/docker-compose.yml"))
                    .withExposedService("minio", 9000)
                    .withExposedService("postgres-int", 5432)
                    .withExposedService("mockoon", 3000);

            dockerComposeContainer.withLogConsumer("minio", new Slf4jLogConsumer(LOGGER).withPrefix("minio"));
            dockerComposeContainer.withLogConsumer("postgres-int", new Slf4jLogConsumer(LOGGER).withPrefix("postgres-int"));
            dockerComposeContainer.withLogConsumer("mockoon", new Slf4jLogConsumer(LOGGER).withPrefix("mockoon"));

            dockerComposeContainer.start();

            return Collections.emptyMap();
        }

        @Override
        public void stop() {
            if (dockerComposeContainer != null) {
                dockerComposeContainer.stop();
            }
        }
    }

}
