


### PreCond
```sh
docker build -f DockerfilePre -t exec-node .

docker run --rm --name initToRunk6 \
-e BLOB_STORAGE_CONN_STRING=${BLOB_STORAGE_CONN_STRING} \
-e COSMOS_RECEIPTS_CONN_STRING=${COSMOS_RECEIPTS_CONN_STRING} \
-e ENVIRONMENT_STRING="${ENVIRONMENT_STRING}" \
exec-node 
```
### TearDown
```sh
docker build -f DockerfilePost -t exec-node .

docker run --rm --name initToRunk6 \
-e BLOB_STORAGE_CONN_STRING=${BLOB_STORAGE_CONN_STRING} \
-e COSMOS_RECEIPTS_CONN_STRING=${COSMOS_RECEIPTS_CONN_STRING} \
-e ENVIRONMENT_STRING="${ENVIRONMENT_STRING}" \
exec-node 
```



