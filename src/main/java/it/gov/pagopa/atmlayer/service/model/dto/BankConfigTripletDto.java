package it.gov.pagopa.atmlayer.service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class BankConfigTripletDto {
    @Size(max = 255)
    private String acquirerId;
    @Size(max = 255)
    private String branchId;
    @Size(max = 255)
    private String terminalId;

    @Override
    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        BankConfigTripletDto bankConfigTripletDto = (BankConfigTripletDto) object;
        return (bankConfigTripletDto.acquirerId.equals(this.acquirerId) &&
                bankConfigTripletDto.branchId.equals(this.branchId) &&
                bankConfigTripletDto.terminalId.equals(this.terminalId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquirerId, branchId, terminalId);
    }
}
