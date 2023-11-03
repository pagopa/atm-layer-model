package it.gov.pagopa.atmlayer.service.model.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class FileStorageS3UtilsTest {

    @BeforeAll
    static void initAll() {
    }
    @BeforeEach
    void init() {
    }

    @Test
    public void buildPutRequest(){
        try {
            PutObjectRequest expectedValue = null;
            String filename="";
            String mimetype="";
            String path="";


            FileStorageS3Utils filestorages3utils  =new FileStorageS3Utils();
            PutObjectRequest actualValue=filestorages3utils.buildPutRequest( filename ,mimetype ,path);
            Assertions.assertNull(actualValue);
        } catch (Exception exception) {
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }

    @Test
    public void buildGetRequest(){
        try {
            GetObjectRequest expectedValue = null;
            String key="";


            FileStorageS3Utils filestorages3utils  =new FileStorageS3Utils();
            GetObjectRequest actualValue=filestorages3utils.buildGetRequest( key);
            Assertions.assertNull(actualValue);
        } catch (Exception exception) {
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }
    @AfterEach
    void tearDown() {
    }
    @AfterAll
    static void tearDownAll() {
    }
}
