package it.gov.pagopa.atmlayer.service.model.mapper;

import io.quarkus.test.junit.QuarkusTest;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceFile;
import it.gov.pagopa.atmlayer.service.model.model.BpmnVersionFrontEndDTO;
import it.gov.pagopa.atmlayer.service.model.model.PageInfo;
import jakarta.inject.Inject;

import it.gov.pagopa.atmlayer.service.model.entity.BpmnVersion;
import it.gov.pagopa.atmlayer.service.model.model.BpmnDTO;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@QuarkusTest
class BpmnVersionMapperTest {

    @Inject
    BpmnVersionMapper bpmnMapper;

    @Test
    void testToDTOList() {

        BpmnVersion bpmnVersion1 = new BpmnVersion();
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        List<BpmnVersion> bpmnVersionList = Arrays.asList(bpmnVersion1, bpmnVersion2);
        List<BpmnDTO> result = bpmnMapper.toDTOList(bpmnVersionList);

        assertEquals(bpmnVersionList.size(), result.size());

    }

    @Test
    void testToFrontEndDTOList() {

        BpmnVersion bpmnVersion1 = new BpmnVersion();
        bpmnVersion1.setModelVersion(1L);
        bpmnVersion1.setResourceFile(new ResourceFile());
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        bpmnVersion2.setModelVersion(1L);
        bpmnVersion2.setResourceFile(new ResourceFile());

        List<BpmnVersion> bpmnVersionList = Arrays.asList(bpmnVersion1, bpmnVersion2);
        List<BpmnVersionFrontEndDTO> result = bpmnMapper.toFrontEndDTOList(bpmnVersionList);

        assertEquals(bpmnVersionList.size(), result.size());

    }

    @Test
    void testToFrontEndDTOListPaged() {

        BpmnVersion bpmnVersion1 = new BpmnVersion();
        bpmnVersion1.setModelVersion(1L);
        bpmnVersion1.setResourceFile(new ResourceFile());
        BpmnVersion bpmnVersion2 = new BpmnVersion();
        bpmnVersion2.setModelVersion(1L);
        bpmnVersion2.setResourceFile(new ResourceFile());
        List<BpmnVersion> bpmnVersionList = Arrays.asList(bpmnVersion1, bpmnVersion2);
        PageInfo<BpmnVersion> pageInfo = new PageInfo<>(0, 1, 2, 2, bpmnVersionList);
        PageInfo<BpmnVersionFrontEndDTO> result = bpmnMapper.toFrontEndDTOListPaged(pageInfo);

        assertEquals(bpmnVersionList.size(), result.getResults().size());

    }

}
