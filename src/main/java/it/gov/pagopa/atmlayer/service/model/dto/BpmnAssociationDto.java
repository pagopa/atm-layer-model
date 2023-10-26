package it.gov.pagopa.atmlayer.service.model.dto;

import it.gov.pagopa.atmlayer.service.model.constraint.BankKeyConstraint;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BpmnAssociationDto {

    private List<@BankKeyConstraint @Valid BankKeyDto> bankKeyDtoList;
}
