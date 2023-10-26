# K6 tests for _ReceiptPdfService_ project

[k6](https://k6.io/) is a load testing tool. 👀 See [here](https://k6.io/docs/get-started/installation/) to install it.

- [01. Receipt service](#01-receipt-service)

This is a set of [k6](https://k6.io) tests related to the _ReceiptPdfService_ initiative.

To invoke k6 test passing parameter use -e (or --env) flag:

```
-e MY_VARIABLE=MY_VALUE
```

## 01. Receipt service

Test the receipt service: 

```
k6 run --env VARS=local.environment.json --env TEST_TYPE=./test-types/load.json --env BLOB_STORAGE_KEY=<your-secret> --env COSMOS_RECEIPT_KEY=<your-secret> script.js //TODO SCRIPT NAME
```

where the mean of the environment variables is:

```json
  "environment": [
    {
      "env": "local",
      "receiptCosmosDBURI": "",
      "receiptDatabaseID":"",
      "receiptContainerID":"",
      "blobStorageAccountURI": "",
      "blobStorageContainerID": "",
      "blobStorageMaxRetry": 5,
      "blobStorageTimeout" : 10,
      "receiptServiceURIBasePath": "",
      "receiptServiceGetAttachmentPath": "/getAttachment",
      "receiptServiceGetAttachmentDetailsPath": "/getAttachmentDetails" 
    }
  ]
```

`receiptCosmosDBURI`: CosmosDB url to access Receipts CosmosDB REST API

`receiptDatabaseID`: database name to access Receipts Cosmos DB REST API

`receiptContainerID`: collection name to access Receipts Cosmos DB REST API

`blobStorageAccountURI`: BlobStorage url to access Receipts Blob Storage REST API

`blobStorageContainerID`: collection name to access Receipts Blob Storage REST API

`blobStorageMaxRetry`: BlobStorage max number of retry

`blobStorageTimeout` : BlobStorage timeout between retries

`receiptServiceURIBasePath`: Receipt Service URI base path

`receiptServiceGetAttachmentPath`: Receipt Service getAttachment path

`receiptServiceGetAttachmentDetailsPath`: Receipt Service getAttachmentDetails path 
