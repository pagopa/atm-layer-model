export const thresholdsSettings = {
  http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
  http_req_duration: ['p(99)<2000'],
};

export const nameThresholds={
  'http_req_duration{name:BPMNcreate}': ['p(95)<500'],
  'http_req_waiting{name:BPMNcreate}':['p(95)<200'],
  'http_req_duration{name:BPMNgetAll}': ['p(95)<6000'],
  'http_req_waiting{name:BPMNgetAll}':['p(95)<6000'],
  'http_req_duration{name:BPMNdeploy}': ['p(95)<1500'],
  'http_req_waiting{name:BPMNdeploy}':['p(95)<300'],
  'http_req_duration{name:BPMNassociate}': ['p(95)<1500'],
  'http_req_waiting{name:BPMNassociate}':['p(95)<00']
}


export const average_load = {
  executor: 'ramping-vus',
  stages: [
    { duration: '2s', target: 20 },
    { duration: '5s', target: 20 },
    { duration: '5s', target: 0 },
  ],
};

export const low_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '2s', target: 1 },
      { duration: '2s', target: 0 },
    ],
  };