package it.gov.pagopa.atml.mil.integration.constraint;

import it.gov.pagopa.atml.mil.integration.utils.BankKeyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = BankKeyValidator.class)
@Documented
public @interface BankKeyConstraint {
    String message() default "{BankKey.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


}
