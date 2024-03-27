package it.gov.pagopa.atmlayer.service.model.validators;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import it.gov.pagopa.atmlayer.service.model.enumeration.UserProfileEnum;
import it.gov.pagopa.atmlayer.service.model.exception.AtmLayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserProfileValidatorTest {
    @InjectMocks
    private UserProfileValidator validator;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateExistenceProfileType() {
        Uni<Void> resultGuest = validator.validateExistenceProfileType(UserProfileEnum.GUEST.getValue());
        assertDoesNotThrow(() -> resultGuest.await().indefinitely());
        Uni<Void> resultOperator = validator.validateExistenceProfileType(UserProfileEnum.OPERATOR.getValue());
        assertDoesNotThrow(() -> resultOperator.await().indefinitely());
        Uni<Void> resultAdmin = validator.validateExistenceProfileType(UserProfileEnum.OPERATOR.getValue());
        assertDoesNotThrow(() -> resultAdmin.await().indefinitely());
        Throwable exception = assertThrowsExactly(AtmLayerException.class, () -> validator.validateExistenceProfileType(4));
    }
}