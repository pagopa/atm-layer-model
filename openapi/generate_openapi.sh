#!/bin/bash

# install api-spec-converter if not present
if [ $(npm list -g | grep -c api-spec-converter) -eq 0 ]; then
  npm install -g api-spec-converter
fi

# save openapi
curl http://localhost:8080/q/openapi | python3 -m json.tool > ./openapi.json

# UI mode http://localhost:8080/swagger-ui/index.html
