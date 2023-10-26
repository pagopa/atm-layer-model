const { BlobServiceClient } = require("@azure/storage-blob");

const blob_storage_conn_string = process.env.RECEIPTS_STORAGE_CONN_STRING || "";
const containerName = process.env.BLOB_STORAGE_CONTAINER_NAME;

const blobServiceClient = BlobServiceClient.fromConnectionString(blob_storage_conn_string);
const containerClient = blobServiceClient.getContainerClient(containerName);

async function createBlobPdf(blobName, content) {

    const blockBlobClient = containerClient.getBlockBlobClient(blobName);

    return await blockBlobClient.upload(content, content.length);
}

async function deleteReceiptAttachment(blobName) {
    const blockBlobClient = containerClient.getBlockBlobClient(blobName);
    // include: Delete the base blob and all of its snapshots.
    // only: Delete only the blob's snapshots and not the blob itself.
    const options = {
        deleteSnapshots: 'include' // or 'only'
    }

    await blockBlobClient.deleteIfExists(options);
}

module.exports = {
    createBlobPdf, deleteReceiptAttachment
}