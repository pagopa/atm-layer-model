import { blobContainerClient,createReceipt, receiptContainer, PARTITION_ID, receiptCosmosDBContainerId } from "./scripts_common.js";

//UPLOAD PDF TO BLOB STORAGE
const uploadDocumentToAzure = async () => {
  const blockBlobClient = blobContainerClient.getBlockBlobClient(PARTITION_ID);
  const response = await blockBlobClient.uploadFile("./resources/testPDF.pdf");
  if (response._response.status !== 201) {
    throw new Error(
      `Error uploading document ${blockBlobClient.name} to container ${blockBlobClient.containerName}`
    );
  }

  return response;
};
uploadDocumentToAzure().then(resp => {
  console.info("RESPONSE SAVE PDF STATUS", resp._response.status);
}) ;


//SAVE RECEIPT WITH BLOB INFO ON COSMOSDB
async function createDocumentInReceiptsDatastore() {
  let event = createReceipt(PARTITION_ID, PARTITION_ID, PARTITION_ID);
  try {
      return await receiptContainer.items.create(event);
  } catch (err) {
    throw new Error(
      `Error saving receipt ${PARTITION_ID} to container ${receiptCosmosDBContainerId}`
    );
  }
}
createDocumentInReceiptsDatastore().then(resp => {
  console.info("RESPONSE SAVE RECEIPT STATUS", resp.statusCode);
});
