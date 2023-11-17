package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@QuarkusTest
public class BpmnVersionMapperTest {

    @Inject
    private BpmnVersionMapper bpmnMapper;

    @Test
    public void testToDTOList() {

        BpmnVersion bpmnVersion1 = new BpmnVersion();
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        List<BpmnVersion> bpmnVersionList = Arrays.asList(bpmnVersion1, bpmnVersion2);
        List<BpmnDTO> result = bpmnMapper.toDTOList(bpmnVersionList);

        assertEquals(bpmnVersionList.size(), result.size());

    }

}
