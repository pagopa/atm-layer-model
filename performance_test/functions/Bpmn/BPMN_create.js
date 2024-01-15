import http from 'k6/http';
import { check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';

export function createBpmn(baseUrl, token, fileBpmn) {
    const fd = new FormData();
    fd.append('filename', 'performance_test');
    fd.append('file', fileBpmn);
    fd.append('functionType', 'MENU');

    const headers = {
        'Content-Type': 'multipart/form-data; boundary=' + fd.boundary,
        'x-api-key': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'BPMNcreate' }
    };

    const response = http.post(`${baseUrl}/bpmn`, fd.body(), params);

    console.log('Response request Resource CREATE:', response.request);
    console.log('Response status Resource CREATE:', response.status);
    console.log('Response body Resource CREATE:', response.body);

    check(response, {
        'response code was 200': (response) => response.status == 200
    });

    return typeof response !== 'undefined' ? response.body : {};
}
