const assert = require('assert');
const { Given, When, Then, After } = require('@cucumber/cucumber');
const {getAttachment, getAttachmentDetails} = require("./common.js");
const { createDocumentInReceiptsDatastore, deleteDocumentFromReceiptsDatastore } = require("./receipts_datastore_client.js");
const { createBlobPdf, deleteReceiptAttachment } = require("./receipts_blob_storage_client.js");

const content = "Hello world!";

// After each Scenario
After(async function () {
  // remove event
  if (this.receiptId != null) {
      await deleteDocumentFromReceiptsDatastore(this.receiptId, this.receiptId);
  }
  if (this.blobName != null) {
    await deleteReceiptAttachment(this.blobName);
}

  this.receiptId = null;
  this.blobName = null;
  this.response = null;
});

//getAttachmentDetails

Given('a receipt with id {string} and debtorFiscalCode {string} stored on receipts datastore', async function (id, fiscalCode) {
  this.receiptId = id;
  // prior cancellation to avoid dirty cases
  await deleteDocumentFromReceiptsDatastore(this.receiptId, this.receiptId);

  let cosmosResponse = await createDocumentInReceiptsDatastore(this.receiptId, fiscalCode);
  assert.strictEqual(cosmosResponse.statusCode, 201);
});

When('an Http GET request is sent to the receipt-service getAttachmentDetails with path value {string} and fiscal_code param with value {string}', async function (receiptId, fiscalCode) {
  this.response = await getAttachmentDetails(receiptId, fiscalCode);
});

When('an Http GET request is sent to the receipt-service getAttachmentDetails without fiscal_code param', async function () {
  this.response = await getAttachmentDetails("id", null);
});


Then('response body contains receipt id {string}', function (receiptId) {
  assert.strictEqual(this.response?.data?.attachments?.[0]?.id, receiptId);
});


//getAttachment


Given('a receipt with id {string} and debtorFiscalCode {string} and mdAttachmentName {string} stored on receipts datastore', async function (id, fiscalCode, blobName) {
  this.receiptId = id;
  // prior cancellation to avoid dirty cases
  await deleteDocumentFromReceiptsDatastore(this.receiptId, this.receiptId);

  let cosmosResponse = await createDocumentInReceiptsDatastore(this.receiptId, fiscalCode, blobName);
  assert.strictEqual(cosmosResponse.statusCode, 201);
});

Given('a pdf with name {string} stored on Blob Storage', async function (blobName) {
  this.blobName = blobName;
  // prior cancellation to avoid dirty cases
  await deleteReceiptAttachment(this.blobName);

  let blobResponse = await createBlobPdf(this.blobName, content);
  assert.strictEqual(blobResponse._response.status, 201);
});

When('an Http GET request is sent to the receipt-service getAttachment with path value {string} and {string} and fiscal_code param with value {string}', async function (receiptId, blobName, fiscalCode) {
  this.response = await getAttachment(receiptId, blobName, fiscalCode);
});

When('an Http GET request is sent to the receipt-service getAttachment without fiscal_code param', async function () {
  this.response = await getAttachment("id", "blobName", null);
});

Then('response body has the expected data content', function () {
  assert.strictEqual(this.response.data, content);
});


//COMMON

Then('response has a {int} Http status', function (expectedStatus) {
  assert.strictEqual(this.response.status, expectedStatus);
});

Then('application error code is {string}', function (expectedAppErrorCode) {
  assert.strictEqual(this.response.data.instance, expectedAppErrorCode);
});