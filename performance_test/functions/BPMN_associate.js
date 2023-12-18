import http from 'k6/http';
import { group, check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';
import { createBpmn } from './BPMN_create.js';
import { generateRandomBpmn, createBpmnAndGetId, generateAssociationBody } from '../utils_functions.js';
import { deployBpmn } from './BPMN_deploy.js';

export function associateRouteBpmn(baseUrl, token, acquirerId, tagName, version) {
    group(tagName, function () {

        const deployedBpmn=deployBpmn(baseUrl, token, 'BPMNdeploy', version);
        const bpmnIdForAssociations=JSON.parse(deployedBpmn).bpmnId;
        const associationBody = generateAssociationBody(bpmnIdForAssociations);

        const fd = new FormData();
        const params = {
            headers: {
                'Content-Type': 'application/json; boundary=' + fd.boundary,
                'x-api-key': token,
            },
            tags: { name: tagName }
        };

        const endPoint = '/bpmn/bank/' + acquirerId + '/associations/function/MENU';
        const url = baseUrl + endPoint;

        const response = http.put(url, associationBody, params);

        console.error();
        console.log('Response status:', response.request);
        console.log('Response status:', response.status);
        console.log('Response body:', response.body);

        check(response, {
            'response code was 200': (response) => response.status == 200,
        });
    })
}