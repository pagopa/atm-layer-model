import { nameThresholds, low_load } from "./options_settings.js";
import { getAllBpmn } from "./functions/Bpmn/BPMN_getAll.js";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { associateRouteBpmn } from "./functions/Bpmn/BPMN_associate.js";
import { updateHtmlResource } from "./functions/Resources/RESOUCRES_update.js";

var appBaseUrl = `${__ENV.MODEL_APPLICATION_BASE_URL}`;
var appBasePath = `${__ENV.MODEL_APPLICATION_BASE_PATH}`;
var token = `${__ENV.MODEL_APPLICATION_KEY}`;


export const options = {
    thresholds: nameThresholds,
    scenarios: { low_load },
}


export function handleSummary(data) {
    return {
        "performance_summary.html": htmlReport(data),
    };
}

export default function () {
    getAllBpmn(appBaseUrl, token);
    associateRouteBpmn(appBaseUrl.concat(appBasePath),token,'performance_acquirer','BPMNassociate',1);
    updateHtmlResource(appBaseUrl.concat(appBasePath), token);
}
