import http from 'k6/http';

const subKey = `${__ENV.OCP_APIM_SUBSCRIPTION_KEY}`;

export function getToService(url, fiscalCode) {

  let headers = {
    'Ocp-Apim-Subscription-Key': subKey,
    fiscal_code: fiscalCode
  };

  return http.get(url, { headers, responseType: "text"});
}

