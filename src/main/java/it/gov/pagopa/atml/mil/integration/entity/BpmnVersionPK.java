package it.gov.pagopa.atml.mil.integration.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class BpmnVersionPK implements Serializable {

    @Serial
    private static final long serialVersionUID = -6327455979830016850L;
    private UUID bpmnId;

    private int modelVersion;
}
