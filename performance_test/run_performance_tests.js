import { nameThresholds, low_load } from "./options_settings.js";
import { getAllBpmn } from "./functions/BPMN_getAll.js";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { associateRouteBpmn } from "./functions/BPMN_associate.js";

const baseUrl = 'https://8o3pf45im8.execute-api.eu-south-1.amazonaws.com/dev';
const relativePath = '/api/v1/model';
const token = 'TfRV0R7jTX1ZhzWxdZBvn2ALZxlhgd446EPfuECc';


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
    getAllBpmn(baseUrl, token);
    associateRouteBpmn(baseUrl.concat(relativePath),token,'performance_acquirer','BPMNassociate',1);
}
