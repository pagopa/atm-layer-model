//package it.gov.pagopa.atmlayer.service.model.constraint;
//
//import it.gov.pagopa.atmlayer.service.model.validators.BankKeyValidator;
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//import jakarta.validation.constraintvalidation.SupportedValidationTarget;
//
//import java.lang.annotation.Documented;
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.Target;
//
//import static jakarta.validation.constraintvalidation.ValidationTarget.ANNOTATED_ELEMENT;
//import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
//import static java.lang.annotation.ElementType.FIELD;
//import static java.lang.annotation.ElementType.METHOD;
//import static java.lang.annotation.ElementType.PARAMETER;
//import static java.lang.annotation.RetentionPolicy.RUNTIME;
//
//@Retention(RUNTIME)
//@Constraint(validatedBy = BankKeyValidator.class)
//@Target({ElementType.TYPE_USE, FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
//@SupportedValidationTarget(ANNOTATED_ELEMENT)
//@Documented
//public @interface BankKeyConstraint {
//    String message() default "{the BankKey is invalid}";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}
