package it.gov.pagopa.atmlayer.service.model.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationKey {

    private String acquirerId;

    private String branchId;

    private String terminalId;
}
