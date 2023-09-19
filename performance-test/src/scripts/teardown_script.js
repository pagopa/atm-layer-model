import { blobContainerClient, receiptContainer, PARTITION_ID } from "./scripts_common.js";

//DELETE PDF FROM BLOB STORAGE
const deleteDocumentFromAzure = async () => {
    const response = await blobContainerClient.deleteBlob(PARTITION_ID);
    if (response._response.status !== 202) {
        throw new Error(`Error deleting PDF ${PARTITION_ID}`);
    }

    return response;
};
deleteDocumentFromAzure().then((res) => {
    console.log("RESPONSE DELETE PDF STATUS", res._response.status);
});


//DELETE RECEIPT FROM COSMOSDB
async function deleteDocumentFromReceiptsDatastore() {
    try {
        return await receiptContainer.item(PARTITION_ID, PARTITION_ID).delete();
    } catch (error) {
        if (error.code !== 404) {
            throw new Error(`Error deleting receipt ${PARTITION_ID}`);
        }
    }
}
deleteDocumentFromReceiptsDatastore().then(resp => {
    console.info("RESPONSE DELETE RECEIPT STATUS", resp.statusCode);
});
