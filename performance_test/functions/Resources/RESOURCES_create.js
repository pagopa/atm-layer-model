import http from 'k6/http';
import { check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';

export function createHtmlResource(baseUrl, token, file) {
    const fd = new FormData();
    fd.append('filename', `performance_test_${new Date().getTime()}.html`);
    fd.append('file', file);
    fd.append('resourceType', 'HTML');
    fd.append('path', 'performanceTest')

    const headers = {
        'Content-Type': 'multipart/form-data; boundary=' + fd.boundary,
        'x-api-key': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'RESOURCECreate' }
    };

    const response = http.post(`${baseUrl}/resources`, fd.body(), params);

    console.log('Response request Resource CREATE:', response.request);
    console.log('Response status Resource CREATE:', response.status);
    console.log('Response body Resource CREATE:', response.body);

    check(response, {
        'response code was 200': (response) => response.status == 200
    });



    return typeof response !== 'undefined' ? response.body : {};
}
