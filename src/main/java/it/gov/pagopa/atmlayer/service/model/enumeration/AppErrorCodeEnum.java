package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.*;

/**
 * Enumeration for application error codes and messages
 */
@Getter
public enum AppErrorCodeEnum {

    ATMLM_500("ATMLM_500", "An unexpected error has occurred, see logs for more info", GENERIC),
    BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000001", "A BPMN file with the same content already exists", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_EXIST("ATMLM_4000002", "The referenced BPMN file does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    BPMN_FILE_NOT_DEPLOYED("ATMLM_4000003", "The referenced BPMN file is not deployed", NOT_DEPLOYED_STATUS),
    BPMN_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000004", "The referenced BPMN file can not be deployed", NOT_DEPLOYABLE_STATUS),
    BPMN_FUNCTION_TYPE_DIFFERENT_FROM_REQUESTED("ATMLM_4000005", "The referenced BPMN file has a function type different from the requested", INVALID_FUNCTION_TYPE),
    BPMN_CANNOT_BE_DELETED_FOR_STATUS("ATMLM_4000006", "The referenced BPMN file can not be deleted in the actual state", NOT_DELETABLE),
    MULTIPLE_BPMN_FILE_FOR_SINGLE_CONFIGURATION("ATMLM_4000007", "Multiple BPMN file found for a single configuration", INTERNAL),
    NO_BPMN_FOUND_FOR_CONFIGURATION("ATMLM_4000008", "No runnable BPMN found for configuration", NOT_VALID_REFERENCED_ENTITY),
    NO_FILE_OR_STORAGE_KEY_FOUND_FOR_BPMN("ATMLM_4000009", "No storage key or file found for BPMN", NOT_VALID_REFERENCED_ENTITY),
    OBJECT_STORE_SAVE_FILE_ERROR("ATMLM_4000010", "Error on persisting file on Object Store ", INTERNAL),
    BPMN_FILE_CANNOT_BE_UPGRADED("ATMLM_4000011", "The referenced BPMN file can not be upgraded", NOT_UPGRADABLE),
    BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS("ATMLM_4000012","A BPMN file with the same Camunda definition key already exists", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY("ATMLM_4000013","BPMN file does not have a definition key",NOT_VALID_FILE),
    MALFORMED_FILE("ATMLM_4000014","Cannot Read Input File",NOT_VALID_FILE),
    SHA256_ERROR("ATMLM_4000015","Cannot calculate SHA256 of Input file",NOT_VALID_FILE),
    DEPLOY_ERROR("ATMLM_4000016","Empty Process Infos in deploy payload",INVALID_DEPLOY),
    NO_CONFIGURATION_FOR_ACQUIRER("ATMLM_4000017","No configuration found for the provided acquirer Id", ID_NOT_FOUND),
    METHOD_NOT_ALLOWED("ATMLM_4000018","Cannot invoke method", INVALID_ARGUMENT),
    RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS("ATMLM_4000019","A resource with the same content already exists", CONSTRAINT_VIOLATION),
    FILE_NOT_SUPPORTED("ATMLM_4000020", "A resource with same file name and path already exists", NOT_UPLOADABLE),
    WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000021","A Workflow Resource file with the same content already exists", CONSTRAINT_VIOLATION),
    WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS("ATMLM_4000022","A Workflow Resource file with the same Camunda definition key already exists", CONSTRAINT_VIOLATION),
    WORKFLOW_FILE_DOES_NOT_EXIST("ATMLM_4000023", "The referenced Workflow Resource file does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS("ATMLM_4000024", "The referenced Workflow Resource file can not be deleted in the actual state", NOT_DELETABLE),
    WORKFLOW_RESOURCE_CANNOT_BE_UPDATED_FOR_STATUS("ATMLM_4000025", "The referenced Workflow Resource file can not be updated in the actual state", NOT_UPDATABLE),
    WORKFLOW_RESOURCE_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000026", "The referenced Workflow Resource file can not be deployed", NOT_DEPLOYABLE_STATUS),
    RESOURCE_WITH_SAME_NAME_AND_PATH_ALREADY_SAVED("ATMLM_4000027", "A resource with same file name and path already exists", NOT_UPLOADABLE),
    RESOURCE_WITH_DIFFERENT_STORAGE_KEY_CANNOT_BE_UPDATED("ATMLM_4000028","The referenced resource has a different storage key: cannot be updated",NOT_UPDATABLE),
    RESOURCE_DOES_NOT_EXIST("ATMLM_4000029", "The referenced Resource does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_UPDATED("ATMLM_4000030", "The referenced Workflow Resource file can not be updated", NOT_UPDATABLE),
    RESOURCE_FILE_DOES_NOT_EXIST("ATMLM_4000031", "The referenced Resource file does not exist", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS("ATMLM_4000032","A workflow resource with the same content already exists", CONSTRAINT_VIOLATION),
    DEPLOYED_FILE_WAS_NOT_RETRIEVED("ATMLM_4000033","Error with Process communication: the referenced file was not retrieved", INTERNAL),
    WORKFLOW_RESOURCE_NOT_DEPLOYED_CANNOT_ROLLBACK("ATMLM_4000034","CamundaDefinitionId of the referenced resource is null: cannot rollback", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_ROLLED_BACK("ATMLM_4000035","Cannot rollback: the referenced resource coincides with the latest deployed version", CANNOT_ROLLBACK),
    MISSING_AWS_ENDPOINT("ATMLM_4000036","Error generating presigned url: No AWS endpoint provided for local configuration", INTERNAL),
    EXTENSION_MISMATCH("ATMLM_4000037","Mismatch between the file extension and the filename", NOT_VALID_FILE),
    BPMN_CANNOT_BE_DISABLED_FOR_ASSOCIATIONS("ATMLM_4000038", "The referenced BPMN file has associations and cannot be disabled", CANNOT_DISABLE),
    BPMN_ALREADY_DISABLED("ATMLM_4000039", "The referenced BPMN file is already disabled", CANNOT_DISABLE);
    private final String errorCode;
    private final String errorMessage;
    private final AppErrorType type;

    AppErrorCodeEnum(String errorCode, String errorMessage, AppErrorType type) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }
}
