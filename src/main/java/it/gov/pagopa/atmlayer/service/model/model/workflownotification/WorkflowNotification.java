package it.gov.pagopa.atmlayer.service.model.model.workflownotification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowNotification {
    String currentActivityName;
    String currentActivityId;
    String requestId;
    String processInstanceId;
    String processDefinitionId;
}
