import http from 'k6/http';
import { group, check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';
import { createBpmnAndGetId } from '../utils_functions.js';

export function deployBpmn(baseUrl, token, tagName, version) {

        const newBpmnUuid = createBpmnAndGetId(baseUrl, token);
        const fd = new FormData();
        const params = {
            headers: {
                'Content-Type': 'application/json; boundary=' + fd.boundary,
                'x-api-key': token,
            },
            tags: { name: tagName }
        };

        const endPoint = '/bpmn/deploy/' + newBpmnUuid + '/version/' + version;
        const url = baseUrl + endPoint;

        const response = http.post(url, fd.body(), params);

        console.error();
        console.log('Response status:', response.request);
        console.log('Response status:', response.status);
        console.log('Response body:', response.body);

        check(response, {
            'response code was 200': (response) => response.status == 200,
        });

        return typeof response !== 'undefined' ? response.body : {};
    
}