package it.gov.pagopa.atmlayer.service.model.service.impl;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import it.gov.pagopa.atmlayer.service.model.entity.ResourceEntity;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import it.gov.pagopa.atmlayer.service.model.mapper.BpmnConfigMapperImpl;
import it.gov.pagopa.atmlayer.service.model.repository.BpmnBankConfigRepository;
import it.gov.pagopa.atmlayer.service.model.repository.ResourceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class ResourceEntityServiceImplTest {

    @Mock
    ResourceEntityRepository resourceEntityRepository;
    @Mock
    ResourceEntityStorageServiceImpl resourceEntityStorageService;

    @InjectMocks
    private ResourceEntityServiceImpl resourceEntityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //How to return PanacheQuery?
//    @Test
//    public void getAll(){
//        ResourceEntity resourceEntity=new ResourceEntity();
//        when(resourceEntityRepository.findAll()).thenReturn((PanacheQuery<ResourceEntity>)resourceEntity);
//        resourceEntityService.getAll()
//                .subscribe().withSubscriber(UniAssertSubscriber.create())
//                .assertCompleted();
//    }

    @Test
    public void uploadFailure(){
        File file=new File("src/test/resources/Test.bpmn");
        when(resourceEntityStorageService.saveFile(any(ResourceEntity.class),any(File.class),any(String.class),any(String.class))).thenReturn(Uni.createFrom().failure(new RuntimeException()));
        resourceEntityService.upload(new ResourceEntity(),file,"filename","path")
                .subscribe().withSubscriber(UniAssertSubscriber.create())
                .assertFailedWith(AtmLayerException.class,"Failed to save Resource Entity in Object Store. Resource creation aborted");
    }
}