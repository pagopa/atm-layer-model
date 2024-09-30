package it.gov.pagopa.atmlayer.service.model.enumeration;

import lombok.Getter;

import static it.gov.pagopa.atmlayer.service.model.enumeration.AppErrorType.*;

/**
 * Enumeration for application error codes and messages
 */
@Getter
public enum AppErrorCodeEnum {
    ATMLM_500("ATMLM_500", "Si è verificato un errore imprevisto, vedere i log per ulteriori informazioni", GENERIC),
    BPMN_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000001", "Una risorsa di processo con lo stesso contenuto esiste già", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_EXIST("ATMLM_4000002", "La risorsa di processo indicata non esiste", NOT_EXISTING_REFERENCED_ENTITY),
    BPMN_FILE_NOT_DEPLOYED("ATMLM_4000003", "La risorsa di processo indicata non è stata rilasciata", NOT_DEPLOYED_STATUS),
    BPMN_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000004", "La risorsa di processo indicata non può essere rilasciata", NOT_DEPLOYABLE_STATUS),
    BPMN_FUNCTION_TYPE_DIFFERENT_FROM_REQUESTED("ATMLM_4000005", "La risorsa di processo indicata ha un tipo di funzione diverso da quello richiesto", INVALID_FUNCTION_TYPE),
    BPMN_CANNOT_BE_DELETED_FOR_STATUS("ATMLM_4000006", "La risorsa di processo indicata non può essere eliminata nello stato attuale", NOT_DELETABLE),
    MULTIPLE_BPMN_FILE_FOR_SINGLE_CONFIGURATION("ATMLM_4000007", "Più risorse di processo trovate per una singola associazione", INTERNAL),
    NO_BPMN_FOUND_FOR_CONFIGURATION("ATMLM_4000008", "Nessuna risorsa di processo eseguibile trovata per l'associazione", NOT_VALID_REFERENCED_ENTITY),
    NO_FILE_OR_STORAGE_KEY_FOUND_FOR_BPMN("ATMLM_4000009", "Nessuna storageKey o file trovato per una risorsa di processo", NOT_VALID_REFERENCED_ENTITY),
    OBJECT_STORE_SAVE_FILE_ERROR("ATMLM_4000010", "Errore nella persistenza del file su Object Store", INTERNAL),
    BPMN_FILE_CANNOT_BE_UPGRADED("ATMLM_4000011", "La risorsa di processo indicata non può essere aggiornata", NOT_UPGRADABLE),
    BPMN_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS("ATMLM_4000012", "Una risorsa di processo con la stessa definitionKey di Camunda esiste già", CONSTRAINT_VIOLATION),
    BPMN_FILE_DOES_NOT_HAVE_DEFINITION_KEY("ATMLM_4000013", "Il file BPMN non ha una definitionKey", NOT_VALID_FILE),
    CANNOT_EXTRACT_FILE_DEFINITION_KEY("ATMLM_4000014", "Impossibile estrarre la definitionKey: il file caricato è malformato o è stato selezionato il tipo di file sbagliato", NOT_VALID_FILE),
    SHA256_ERROR("ATMLM_4000015", "Impossibile calcolare SHA256 del file di input", NOT_VALID_FILE),
    DEPLOY_ERROR("ATMLM_4000016", "Informazioni sul processo vuote nel payload di deploy", INVALID_DEPLOY),
    NO_CONFIGURATION_FOR_ACQUIRER("ATMLM_4000017", "Nessuna configurazione trovata per la banca fornita", ID_NOT_FOUND),
    METHOD_NOT_ALLOWED("ATMLM_4000018", "Impossibile invocare il metodo", INVALID_ARGUMENT),
    RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS("ATMLM_4000019", "Una risorsa con lo stesso contenuto esiste già", CONSTRAINT_VIOLATION),
    FILE_NOT_SUPPORTED("ATMLM_4000020", "Una risorsa con lo stesso nome file e percorso esiste già", NOT_UPLOADABLE),
    WORKFLOW_RESOURCE_FILE_WITH_SAME_CONTENT_ALREADY_EXIST("ATMLM_4000021", "Una risorsa aggiuntiva di processo con lo stesso contenuto esiste già", CONSTRAINT_VIOLATION),
    WORKFLOW_RESOURCE_FILE_WITH_SAME_CAMUNDA_DEFINITION_KEY_ALREADY_EXISTS("ATMLM_4000022", "Una risorsa aggiuntiva di processo con la stessa definitionKey di Camunda esiste già", CONSTRAINT_VIOLATION),
    WORKFLOW_FILE_DOES_NOT_EXIST("ATMLM_4000023", "La risorsa aggiuntiva di processo indicata non esiste", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_DELETED_FOR_STATUS("ATMLM_4000024", "La risorsa aggiuntiva di processo indicata non può essere eliminata nello stato attuale", NOT_DELETABLE),
    WORKFLOW_RESOURCE_CANNOT_BE_UPDATED_FOR_STATUS("ATMLM_4000025", "La risorsa aggiuntiva di processo indicata non può essere aggiornata nello stato attuale", NOT_UPDATABLE),
    WORKFLOW_RESOURCE_FILE_CANNOT_BE_DEPLOYED("ATMLM_4000026", "La risorsa aggiuntiva di processo indicata non può essere rilasciata", NOT_DEPLOYABLE_STATUS),
    RESOURCE_WITH_SAME_NAME_AND_PATH_ALREADY_SAVED("ATMLM_4000027", "Una risorsa con lo stesso nome file e percorso è già stata salvata", NOT_UPLOADABLE),
    RESOURCE_WITH_DIFFERENT_STORAGE_KEY_CANNOT_BE_UPDATED("ATMLM_4000028", "La risorsa indicata ha una chiave di archiviazione diversa: non può essere aggiornata", NOT_UPDATABLE),
    RESOURCE_DOES_NOT_EXIST("ATMLM_4000029", "La risorsa indicata non esiste", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_UPDATED("ATMLM_4000030", "La risorsa aggiuntiva di processo indicata non può essere aggiornata", NOT_UPDATABLE),
    RESOURCE_FILE_DOES_NOT_EXIST("ATMLM_4000031", "La risorsa statica indicata non esiste", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_WITH_SAME_SHA256_ALREADY_EXISTS("ATMLM_4000032", "Una risorsa aggiuntiva per processi con lo stesso contenuto esiste già", CONSTRAINT_VIOLATION),
    DEPLOYED_FILE_WAS_NOT_RETRIEVED("ATMLM_4000033", "Errore nella comunicazione del processo: il file indicato non è stato recuperato", INTERNAL),
    WORKFLOW_RESOURCE_NOT_DEPLOYED_CANNOT_ROLLBACK("ATMLM_4000034", "CamundaDefinitionId della risorsa indicata è nullo: impossibile eseguire il rollback", NOT_EXISTING_REFERENCED_ENTITY),
    WORKFLOW_RESOURCE_CANNOT_BE_ROLLED_BACK("ATMLM_4000035", "Impossibile eseguire il rollback: la risorsa indicata coincide con l'ultima versione rilasciata", CANNOT_ROLLBACK),
    MISSING_AWS_ENDPOINT("ATMLM_4000036", "Errore nella generazione del presigned url: nessun endpoint AWS fornito per l'associazione locale", INTERNAL),
    EXTENSION_MISMATCH("ATMLM_4000037", "Discordanza tra l'estensione del file e il nome del file", NOT_VALID_FILE),
    BPMN_CANNOT_BE_DISABLED_FOR_ASSOCIATIONS("ATMLM_4000038", "La risorsa di processo indicata ha associazioni e non può essere disabilitata", CANNOT_DISABLE),
    BPMN_ALREADY_DISABLED("ATMLM_4000039", "La risorsa di processo indicata è già disabilitata", CANNOT_DISABLE),
    DUPLICATE_ASSOCIATION_CONFIGS("ATMLM_4000040", "Impossibile salvare le associazioni: banca/filiale/terminale duplicata in input", INVALID_ARGUMENT),
    PAGE_SIZE_WRONG_VALUE("ATMLM_4000041", "Pagina e dimensione non devono essere nulli o vuoti, e la dimensione deve essere maggiore di zero", INVALID_ARGUMENT),
    ILLEGAL_CONFIGURATION_TRIPLET("ATMLM_4000046", "L'id della banca deve essere specificato per l'id della filiale, e l'id della filiale deve essere specificato per l'id del terminale", INVALID_ARGUMENT),
    CONFIGURATION_TRIPLET_ALREADY_ASSOCIATED("ATMLM_4000047", "La banca/filiale/terminale indicata è già associata a una risorsa di processo", CANNOT_ASSOCIATE),
    CONFIGURATION_DOES_NOT_EXIST("ATMLM_4000048", "La banca/filiale/terminale indicata non esiste", NOT_EXISTING_REFERENCED_ENTITY),
    CONFIGURATION_TRIPLET_NOT_ASSOCIATED("ATMLM_4000049", "La banca/filiale/terminale indicata non ha associazioni per il tipo di funzione indicato. Creare un'associazione prima di sostituirla", CANNOT_REPLACE_ASSOCIATION),
    WORKFLOW_RESOURCE_INTERNAL_ERROR("ATMLM_4000050", "Nessun file associato alla risorsa aggiuntiva di processo o nessuna storageKey trovata", INTERNAL),
    BPMN_INTERNAL_ERROR("ATMLM_4000051", "Nessun file associato a BPMN o nessuna storageKey trovata", INTERNAL),
    BPMN_FILE_CANNOT_BE_UNDEPLOYED("ATMLM_4000052", "La risorsa di processo indicata non può essere rilasciata", INTERNAL),
    NO_ASSOCIATION_FOUND("ATMLM_4000060","Nessuna associazione trovata", CONSTRAINT_VIOLATION),
    ALL_FIELDS_ARE_BLANK("ATMLM_4000061", "Tutti i campi sono vuoti", AppErrorType.BLANK_FIELDS),
    RESOURCES_CREATION_ERROR("ATMLM_4000062", "Errore nella creazione di resource multipli", GENERIC ),
    FILE_DECODE_ERROR("ATMLM_4000063", "Errore nella decodifica del file", GENERIC ),
    DATABASE_SAVE_FILE_ERROR("ATMLM_4000064", "Errore nella persistenza del file sul database", INTERNAL),
    OBJECT_STORE_COPY_FILE_ERROR("ATMLM_4000065", "Errore nella copia del file nella cartella DELETE su Object Store", INTERNAL);
    private final String errorCode;
    private final String errorMessage;
    private final AppErrorType type;

    AppErrorCodeEnum(String errorCode, String errorMessage, AppErrorType type) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }
}
