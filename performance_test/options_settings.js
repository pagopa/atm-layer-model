export const thresholdsSettings = {
  http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
  http_req_duration: ['p(99)<2000'],
};

export const nameThresholds={

  'http_req_duration{name:BPMNcreate}': ['p(95)<10000'],
  'http_req_waiting{name:BPMNcreate}':['p(95)<10000'],
  'http_req_failed{name:BPMNcreate}':['rate<0.01'],
  'http_reqs{name:BPMNcreate}':[],

  // 'http_req_duration{name:BPMNgetAll}': ['p(95)<6000'],
  // 'http_req_waiting{name:BPMNgetAll}':['p(95)<6000'],
  // 'http_req_failed{name:BPMNgetAll}':['rate<0.01'],
  // 'http_reqs{name:BPMNgetAll}':[],

  'http_req_duration{name:BPMNdeploy}': ['p(95)<10000'],
  'http_req_waiting{name:BPMNdeploy}':['p(95)<10000'],
  'http_req_failed{name:BPMNdeploy}':['rate<0.5'],
  'http_reqs{name:BPMNdeploy}':[],

  'http_req_duration{name:BPMNassociate}': ['p(95)<10000'],
  'http_req_waiting{name:BPMNassociate}':['p(95)<10000'],
  'http_req_failed{name:BPMNassociate}':['rate<0.01'],
  'http_reqs{name:BPMNassociate}':[],

  'http_req_duration{name:BPMNupgrade}': ['p(95)<10000'],
  'http_req_waiting{name:BPMNupgrade}':['p(95)<10000'],
  'http_req_failed{name:BPMNupgrade}':['rate<0.01'],
  'http_reqs{name:BPMNupgrade}':[],

  // RESOURCES

  'http_req_duration{name:RESOURCECreate}': ['p(95)<10000'],
  'http_req_waiting{name:RESOURCECreate}':['p(95)<10000'],
  'http_req_failed{name:RESOURCECreate}':['rate<0.01'],
  'http_reqs{name:RESOURCECreate}':[],

  'http_req_duration{name:RESOURCEUpdate}': ['p(95)<10000'],
  'http_req_waiting{name:RESOURCEUpdate}':['p(95)<10000'],

  'http_req_failed{name:RESOURCEUpdate}':['rate<0.01'],
  'http_reqs{name:RESOURCEUpdate}':[]
}


export const average_load = {
  executor: 'ramping-vus',
  stages: [
    { duration: '1m', target: 1 },
    { duration: '10s', target: 3 },
    { duration: '30s', target: 2 },
  ],
};

export const low_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '2s', target: 1 },
      { duration: '2s', target: 0 },
    ],
  };