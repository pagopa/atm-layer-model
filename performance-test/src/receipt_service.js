import { check } from 'k6';
import { getToService } from './modules/receipt_service_client.js';
import { SharedArray } from 'k6/data';

const varsArray = new SharedArray('vars', function () {
    return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
export const ENV_VARS = varsArray[0];
export let options = JSON.parse(open(__ENV.TEST_TYPE));

const fiscalCode = "JHNDOE00A01F205N";
let receiptId = ENV_VARS.receiptTestId;
let attachmentUrl = "";

const receiptServiceURIBasePath = `${ENV_VARS.receiptServiceURIBasePath}`;

export default function () {
    //getAttachmentDetails
    let response = getToService(`${receiptServiceURIBasePath}/${receiptId}`, fiscalCode);
    console.info("Receipt Service getAttachmentDetails call, Status " + response.status);

    let responseBody = JSON.parse(response.body);

    attachmentUrl = responseBody &&
        responseBody.attachments.length > 0 &&
        responseBody.attachments[0] &&
        responseBody.attachments[0].url

    check(response, {
        'Receipt Service getAttachmentDetails status is 200': () => response.status === 200,
        'Receipt Service getAttachmentDetails body has attachment url': () =>
            attachmentUrl
    });

    if (
        attachmentUrl
    ) {
        //getAttachment
        response = getToService(`${receiptServiceURIBasePath}/${receiptId}/${attachmentUrl}`, fiscalCode);

        console.log("Receipt Service getAttachment call, Status " + response.status);

        check(response, {
            'Receipt Service getAttachment status is 200': (response) => response.status === 200,
            'Receipt Service getAttachment content_type is the expected application/pdf': (response) => response.headers["Content-Type"] === "application/pdf",
            'Receipt Service getAttachment body not null': (response) => response.body !== null
        });
    }

}