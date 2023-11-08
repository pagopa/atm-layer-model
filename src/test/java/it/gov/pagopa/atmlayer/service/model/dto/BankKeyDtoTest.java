package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BankKeyDtoTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void testAcquirerIdNotNull() {
    BankKeyDto bankKeyDto = new BankKeyDto();
    Set<ConstraintViolation<BankKeyDto>> violations = validator.validate(bankKeyDto);
    assertTrue(violations.stream()
        .anyMatch(violation -> "The acquirerId cannot be null".equals(violation.getMessage())));
    assertEquals(1, violations.size());
  }

  @Test
  public void testGettersAndSetters(){
    BankKeyDto bankKeyDto = new BankKeyDto();
    bankKeyDto.setAcquirerId("12345");
    bankKeyDto.setBranches(null);
    assertEquals("12345", bankKeyDto.getAcquirerId());
    assertNull(bankKeyDto.getBranches());
  }

  @Test
  public void testEqualsAndHashCode(){
    BankKeyDto bankKeyDto1 = new BankKeyDto();
    bankKeyDto1.setAcquirerId("12345");
    bankKeyDto1.setBranches(null);
    BankKeyDto bankKeyDto2 = new BankKeyDto();
    bankKeyDto2.setAcquirerId("12345");
    bankKeyDto2.setBranches(null);
    assertEquals(bankKeyDto1, bankKeyDto2);
    assertEquals(bankKeyDto2, bankKeyDto1);
    assertEquals(bankKeyDto1.hashCode(), bankKeyDto2.hashCode());
  }

  @Test
  public void testToString(){
    BankKeyDto bankKeyDto = new BankKeyDto();
    bankKeyDto.setAcquirerId("12345");
    bankKeyDto.setBranches(null);
    assertEquals("BankKeyDto(acquirerId=12345, branches=null)", bankKeyDto.toString());
  }
}
