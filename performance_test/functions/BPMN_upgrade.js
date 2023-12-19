import http from 'k6/http';
import { check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';

export function upgradeBpmn(uuid, fileBpmn, token, baseUrl, tagName) {
    const fd = new FormData();
    fd.append('uuid', uuid);
    fd.append('filename', 'performance_test');
    fd.append('file', fileBpmn);
    fd.append('functionType', 'MENU');

    const headers = {
        'Content-Type': 'multipart/form-data; boundary=' + fd.boundary,
        'x-api-key': token,
    };
    
    const endPoint = '/bpmn/upgrade';
    const url = baseUrl + endPoint;

    const response = http.post(
        url,
        fd.body(),
        {
            headers: headers,
            tags: { name: tagName }
        },
    );

    console.log('Response status UPGRADE:', response.request);
    console.log('Response status UPGRADE:', response.status);
    console.log('Response body UPGRADE:', response.body);

    check(response, {
        'response code was 200': (response) => response.status == 200
    });

    //return typeof response !== 'undefined' ? response.body : {};
}
