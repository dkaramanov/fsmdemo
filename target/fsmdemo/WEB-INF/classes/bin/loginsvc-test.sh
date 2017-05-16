#/bin/sh
PORT=$1
URI=$2

curl -i --data "@SupplierManagement.scr" http://localhost:${PORT}${URI}/____eppLoad____PartnershipSuppliers____loginsvc
sleep 1
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____accept_connection__from_requestor
sleep 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"login","from":"requestor","parameters":{"param1":{"username":"steve"},"param2":{"password":"xyz"}}}' http://localhost:${PORT}${URI}/____receiveMessage
sleep 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"loginsuccess","to":"requestor"}' http://localhost:${PORT}${URI}/____sendMessage

