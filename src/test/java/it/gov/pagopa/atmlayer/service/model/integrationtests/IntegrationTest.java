package it.gov.pagopa.atmlayer.service.model.integrationtests;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(value = EnvironmentTestServicesResource.DockerCompose.class, restrictToAnnotatedClass = true)
@Slf4j
public class IntegrationTest {


    public static GenericContainer<?> NEWMAN;

    @Inject
    BucketCreationUtils bucketCreationUtils;


    @BeforeAll
    static void exposeTestPort() {
        Testcontainers.exposeHostPorts(8086);
        NEWMAN = new GenericContainer<>(new ImageFromDockerfile()
                .withDockerfile(Paths.get("src/test/resources/integration-test/Dockerfile-postman")))
                .withFileSystemBind("src/test/resources/integration-test/output", "/output", BindMode.READ_WRITE)
                .withAccessToHost(true)
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(120)));
    }

    @Test
    void executePostmanCollectionWithNewmann() {

        bucketCreationUtils.createBucketIfNotExisting();
        NEWMAN.start();
        log.info(NEWMAN.getLogs());
        assertEquals(0, NEWMAN.getCurrentContainerInfo().getState().getExitCodeLong());
    }

}
