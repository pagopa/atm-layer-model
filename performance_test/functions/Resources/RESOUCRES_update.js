import http from 'k6/http';
import { check } from 'k6';
import { FormData } from 'https://jslib.k6.io/formdata/0.0.2/index.js';
import { generateRandomHTML, generatedHTMLResource } from '../../utils_functions.js';

export function updateHtmlResource(baseUrl, token) {
    const fd = new FormData();
    const resourceId = generatedHTMLResource(baseUrl, token).resourceId;

    fd.append('file', generateRandomHTML());

    const headers = {
        'Content-Type': 'multipart/form-data; boundary=' + fd.boundary,
        'x-api-key': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'RESOURCEUpdate' }
    };

    const response = http.put(`${baseUrl}/resources/${resourceId}`, fd.body(), params);

    console.error();
    console.log('Response request Resource UPDATE:', response.request);
    console.log('Response status Resource UPDATE:', response.status);
    console.log('Response body Resource UPDATE:', response.body);

    check(response, {
        'response code was 200': (response) => response.status == 200
    });
}
