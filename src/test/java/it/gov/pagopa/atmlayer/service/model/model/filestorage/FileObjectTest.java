package it.gov.pagopa.atmlayer.service.model.model.filestorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.S3Object;

@QuarkusTest
class FileObjectTest {

    @Test
    void from_ShouldCreateFileObjectFromS3Object() {
        String objectKey = "testObjectKey";
        Long size = 1024L;
        S3Object s3Object = mock(S3Object.class);
        when(s3Object.key()).thenReturn(objectKey);
        when(s3Object.size()).thenReturn(size);
        FileObject file = FileObject.from(s3Object);
        assertEquals(objectKey, file.getObjectKey());
        assertEquals(size, file.getSize());
    }

    @Test
    void from_ShouldReturnEmptyFileObjectForNullS3Object() {
        S3Object s3Object = null;
        FileObject file = FileObject.from(s3Object);
        assertNull(file.getObjectKey());
        assertNull(file.getSize());
    }

    @Test
    void getObjectKey_ShouldReturnObjectKey() {
        FileObject file = new FileObject();
        file.setObjectKey("testObjectKey");
        String result = file.getObjectKey();
        assertEquals("testObjectKey", result);
    }

    @Test
    void getSize_ShouldReturnSize() {
        FileObject file = new FileObject();
        file.setSize(1024L);
        Long result = file.getSize();
        assertEquals(1024L, result);
    }

    @Test
    void setObjectKey_ShouldSetObjectKey() {
        FileObject file = new FileObject();
        file.setObjectKey("testObjectKey");
        assertEquals("testObjectKey", file.getObjectKey());
    }

    @Test
    void setSize_ShouldSetSize() {
        FileObject file = new FileObject();
        file.setSize(1024L);
        assertEquals(1024L, file.getSize());
    }
}
