import http from 'k6/http';
import { group, check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';
import { createBpmn } from './BPMN_create.js';
import { generateRandomBpmn, createBpmnAndGetId, generateAssociationBody, generateUpgradedBpmnByDefKey } from '../utils_functions.js';
import { deployBpmn } from './BPMN_deploy.js';
import { upgradeBpmn } from './BPMN_upgrade.js';

export function associateRouteBpmn(baseUrl, token, acquirerId, tagName, version) {
    group(tagName, function () {

        const deployedBpmn=deployBpmn(baseUrl, token, 'BPMNdeploy', version);
        const deployedBpmnId=JSON.parse(deployedBpmn).bpmnId;
        const deployedBpmnDefKey=JSON.parse(deployedBpmn).definitionKey;
        upgradeBpmn(deployedBpmnId, generateUpgradedBpmnByDefKey(deployedBpmnDefKey), token, baseUrl, 'BPMNupgrade');
        const associationBody = generateAssociationBody(deployedBpmnId);

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
        console.log('Response status ASSOCIATE:', response.request);
        console.log('Response status ASSOCIATE:', response.status);
        console.log('Response body ASSOCIATE:', response.body);

        check(response, {
            'response code was 200': (response) => response.status == 200,
        });
    })
}