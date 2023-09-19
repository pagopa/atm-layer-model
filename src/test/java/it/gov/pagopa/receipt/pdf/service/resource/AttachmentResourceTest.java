package it.gov.pagopa.receipt.pdf.service.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import it.gov.pagopa.receipt.pdf.service.exception.AttachmentNotFoundException;
import it.gov.pagopa.receipt.pdf.service.exception.FiscalCodeNotAuthorizedException;
import it.gov.pagopa.receipt.pdf.service.exception.ReceiptNotFoundException;
import it.gov.pagopa.receipt.pdf.service.model.Attachment;
import it.gov.pagopa.receipt.pdf.service.model.AttachmentsDetailsResponse;
import it.gov.pagopa.receipt.pdf.service.model.ErrorResponse;
import it.gov.pagopa.receipt.pdf.service.service.AttachmentsService;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static it.gov.pagopa.receipt.pdf.service.enumeration.AppErrorCodeEnum.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@QuarkusTest
class AttachmentResourceTest {

    private static final String THIRD_PARTY_ID = "test-id";
    private static final String FISCAL_CODE = "AAAAAAAAAAAAAAAA";
    private static final String INVALID_FISCAL_CODE = "tooShort";
    private static final String ATTACHMENT_URL = "url";

    @InjectMock(convertScopes = true)
    private AttachmentsService attachmentsServiceMock;

    @Inject
    private AttachmentResource sut;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void getAttachmentDetailsSuccess() {

        AttachmentsDetailsResponse attachment = AttachmentsDetailsResponse.builder()
                .attachments(
                        Collections.singletonList(
                                Attachment.builder()
                                        .id(THIRD_PARTY_ID)
                                        .contentType("application/pdf")
                                        .name(UUID.randomUUID().toString())
                                        .url(UUID.randomUUID().toString())
                                        .build()
                        )
                ).build();

        doReturn(attachment).when(attachmentsServiceMock).getAttachmentsDetails(THIRD_PARTY_ID, FISCAL_CODE);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(200)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        AttachmentsDetailsResponse response = objectMapper.readValue(responseString, AttachmentsDetailsResponse.class);
        assertNotNull(response);
        assertNotNull(response.getAttachments());
        assertEquals(1, response.getAttachments().size());
        assertEquals(THIRD_PARTY_ID, response.getAttachments().get(0).getId());

    }

    @Test
    @SneakyThrows
    void getAttachmentDetailsFailMissingFiscalCodeHeader() {
        String responseString =
                given()
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_901.getErrorCode(), response.getInstance());
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(BAD_REQUEST.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentDetailsFailInvalidFiscalCodeHeader() {
        String responseString =
                given()
                        .queryParam("fiscal_code", INVALID_FISCAL_CODE)
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_901.getErrorCode(), response.getInstance());
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(BAD_REQUEST.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentDetailsFailGetReceiptError400() {
        doThrow(new ReceiptNotFoundException(PDFS_800, "")).when(attachmentsServiceMock).getAttachmentsDetails(THIRD_PARTY_ID, FISCAL_CODE);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(404)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_800.getErrorCode(), response.getInstance());
        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(NOT_FOUND.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());
    }

    @Test
    @SneakyThrows
    void getAttachmentDetailsFailGetReceiptError500() {
        doThrow(new FiscalCodeNotAuthorizedException(PDFS_700, "")).when(attachmentsServiceMock).getAttachmentsDetails(THIRD_PARTY_ID, FISCAL_CODE);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(500)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_700.getErrorCode(), response.getInstance());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());
    }

    @Test
    @SneakyThrows
    void getAttachmentDetailsFailWithUnexpectedError() {
        doThrow(IllegalArgumentException.class).when(attachmentsServiceMock).getAttachmentsDetails(THIRD_PARTY_ID, FISCAL_CODE);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get("/messages/" + THIRD_PARTY_ID)
                        .then()
                        .statusCode(500)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_400.getErrorCode(), response.getInstance());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());
    }

    @Test
    @SneakyThrows
    void getAttachmentSuccess() {
        File tempDirectory = Files.createTempDirectory("test").toFile();
        File file = Files.createTempFile(tempDirectory.toPath(), "receipt", ".pdf").toFile();

        doReturn(file).when(attachmentsServiceMock).getAttachment(THIRD_PARTY_ID, FISCAL_CODE, ATTACHMENT_URL);

        byte[] response =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(200)
                        .contentType("application/pdf")
                        .header("content-disposition", "attachment;")
                        .extract()
                        .asByteArray();


        assertNotNull(response);
        assertFalse(file.exists());
        assertFalse(tempDirectory.exists());

    }

    @Test
    @SneakyThrows
    void getAttachmentFailMissingFiscalCodeHeader() {
        String responseString =
                given()
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_901.getErrorCode(), response.getInstance());
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(BAD_REQUEST.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentFailInvalidFiscalCodeHeader() {
        String responseString =
                given()
                        .queryParam("fiscal_code", INVALID_FISCAL_CODE)
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_901.getErrorCode(), response.getInstance());
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(BAD_REQUEST.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentFailGetReceiptError400() {
        doThrow(new AttachmentNotFoundException(PDFS_602, "")).when(attachmentsServiceMock).getAttachment(THIRD_PARTY_ID, FISCAL_CODE, ATTACHMENT_URL);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(404)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_602.getErrorCode(), response.getInstance());
        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(NOT_FOUND.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentFailGetReceiptError500() {
        doThrow(new FiscalCodeNotAuthorizedException(PDFS_706, "")).when(attachmentsServiceMock).getAttachment(THIRD_PARTY_ID, FISCAL_CODE, ATTACHMENT_URL);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(500)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_706.getErrorCode(), response.getInstance());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());

    }

    @Test
    @SneakyThrows
    void getAttachmentFailReadingAttachmentFileContent() {
        doReturn(new File("")).when(attachmentsServiceMock).getAttachment(THIRD_PARTY_ID, FISCAL_CODE, ATTACHMENT_URL);

        String responseString =
                given()
                        .queryParam("fiscal_code", FISCAL_CODE)
                        .when().get(String.format("/messages/%s/%s", THIRD_PARTY_ID, ATTACHMENT_URL))
                        .then()
                        .statusCode(500)
                        .contentType("application/json")
                        .extract()
                        .asString();


        assertNotNull(responseString);
        ErrorResponse response = objectMapper.readValue(responseString, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(PDFS_500.getErrorCode(), response.getInstance());
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getTitle());
        assertNotNull(response.getDetail());
        assertNotNull(response.getTitle());
    }
}