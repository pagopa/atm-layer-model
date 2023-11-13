package it.gov.pagopa.atmlayer.service.model.integrationtests;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
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

@QuarkusTest
@QuarkusTestResource(value = EnvironmentTestServicesResource.DockerCompose.class, restrictToAnnotatedClass = true)
@Slf4j
public class IntegrationTests {

    private static final GenericContainer<?> NEWMAN = new GenericContainer<>(new ImageFromDockerfile()
            .withDockerfile(Paths.get("src/test/resources/integration-test/Dockerfile-postman")))
            .withFileSystemBind("src/test/resources/integration-test/output", "/output", BindMode.READ_WRITE)
            .withAccessToHost(true)
            .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(120)));

    @BeforeAll
    static void exposeTestPort() {
        Testcontainers.exposeHostPorts(8086);
    }

    @Test
    void executePostmanCollectionWithNewmann() {
        NEWMAN.start();
        log.info(NEWMAN.getLogs());
        assertTrue(NEWMAN.getCurrentContainerInfo().getState().getExitCodeLong() == 0);
    }

}
