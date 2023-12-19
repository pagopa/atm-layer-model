import http from 'k6/http';
import { group, check } from 'k6';


export function getAllBpmn(baseUrl, token) {
  group('BPMNgetAll', function () {

    const params = {
      headers: {
        'x-api-key': token,
      },
      tags: { name: 'BPMNgetAll' },
    };

    let res = http.get(`${baseUrl}/api/v1/model/bpmn`, params);

    console.log(`Get All BPMN request duration: ${res.timings.duration} ms`);

    check(res, {
      'response code was 200': (res) => res.status == 200,
    });
  });
}