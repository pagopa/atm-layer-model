import { BlobServiceClient } from "@azure/storage-blob";
import { CosmosClient } from "@azure/cosmos";
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);

//ENVIRONMENTAL VARIABLES
const blobStorageConnString = process.env.BLOB_STORAGE_CONN_STRING;
const receiptCosmosDBConnString = process.env.COSMOS_RECEIPTS_CONN_STRING;

const environmentString = process.env.ENVIRONMENT_STRING || "local";
let environmentVars = require(`../${environmentString}.environment.json`)?.environment?.[0] || {};

const blobStorageContainerID = environmentVars.blobStorageContainerID;
const receiptCosmosDBDatabaseId = environmentVars.receiptDatabaseID;
export const receiptCosmosDBContainerId = environmentVars.receiptContainerID;

//CONSTANTS
export const PARTITION_ID = environmentVars.receiptTestId;

//CLIENTS
const blobServiceClient = BlobServiceClient.fromConnectionString(
    blobStorageConnString || ""
);
export const blobContainerClient = blobServiceClient.getContainerClient(
    blobStorageContainerID || ""
);
const client = new CosmosClient(receiptCosmosDBConnString);
export const receiptContainer = client.database(receiptCosmosDBDatabaseId).container(receiptCosmosDBContainerId);

//METHODS
export function createReceipt(id, pdfName, pdfUrl) {
	let receipt =
	{
        "id" : id,
		"eventId": id,
		"eventData": {
			"payerFiscalCode": "JHNDOE00A01F205N",
			"debtorFiscalCode": "JHNDOE00A01F205N"
		},
        "mdAttach":{
            name: pdfName,
            url: pdfUrl
        },
		"status": "IO_NOTIFIED",
		"numRetry": 0
	}
	return receipt
}

export const fileToBase64 = (filename, filepath) => {
    return new Promise(resolve => {
      let file = new File([filename], filepath);
      let reader = new FileReader();
      // Read file content on file loaded event
      reader.onload = function(event) {
        resolve(event.target.result);
      };
      
      // Convert data to base64 
      reader.readAsDataURL(file);
    });
};

export const converPdfToBase64 = (filename, filepath) => {
    let finalResult;
    fileToBase64(filename, filepath).then(result => {
        finalResult = result;
    })

    return finalResult;
};