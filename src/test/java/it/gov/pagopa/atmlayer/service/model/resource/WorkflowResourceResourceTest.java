package it.gov.pagopa.atmlayer.service.model.resource;

import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.entity.WorkflowResource;
import it.gov.pagopa.atmlayer.service.model.mapper.WorkflowResourceMapper;
import it.gov.pagopa.atmlayer.service.model.model.WorkflowResourceDTO;
import it.gov.pagopa.atmlayer.service.model.service.WorkflowResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WorkflowResourceResourceTest {

  @Mock
  private WorkflowResourceService workflowResourceService;

  @Mock
  private WorkflowResourceMapper workflowResourceMapper;

  @InjectMocks
  private WorkflowResourceResource workflowResourceResource;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAll() {
    List<WorkflowResource> mockList = Collections.emptyList();
    List<WorkflowResourceDTO> mockDTOList = Collections.emptyList();
    when(workflowResourceService.getAll()).thenReturn(Uni.createFrom().item(mockList));
    when(workflowResourceMapper.toDTOList(mockList)).thenReturn(mockDTOList);
    Uni<List<WorkflowResourceDTO>> result = workflowResourceResource.getAll();
    assertNotNull(result);
    assertSame(mockDTOList, result.await().indefinitely());
  }

  @Test
  void testGetById() {
    UUID mockUuid = UUID.randomUUID();
    WorkflowResource mockEntity = Mockito.mock(WorkflowResource.class);
    WorkflowResourceDTO mockDTO = Mockito.mock(WorkflowResourceDTO.class);
    when(workflowResourceService.findById(any(UUID.class))).thenReturn(
        Uni.createFrom().item(java.util.Optional.of(mockEntity)));
    when(workflowResourceMapper.toDTO(mockEntity)).thenReturn(mockDTO);
    Uni<WorkflowResourceDTO> result = workflowResourceResource.getById(mockUuid);
    assertNotNull(result);
    assertSame(mockDTO, result.await().indefinitely());
  }
}
