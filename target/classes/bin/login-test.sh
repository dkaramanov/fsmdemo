#/bin/sh
PORT=$1
URI=$2

echo "POSTING SupplierManagement.scr for PartnershipSuppliers and projecting loginsvc"
echo -n ?; read x
curl -i --data "@SupplierManagement.scr" http://localhost:${PORT}${URI}/____eppLoad____PartnershipSuppliers____loginsvc
echo -n ?; read x
echo "POSTING accept_connection__from_requestor"
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____accept_connection__from_requestor
echo -n ?; read x
echo "POSTING receiveMessage: '{"function":"login","from":"requestor","parameters":{"param1":{"username":"steve"}},"param2":{"password":"xyz"}}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"login","from":"requestor","parameters":{"param1":{"username":"steve"},"param2":{"password":"xyz"}}}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x
echo "POSTING sendMessage: '{"function":"loginfailure","to":"requestor"}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"loginsuccess","to":"requestor"}' http://localhost:${PORT}${URI}/____sendMessage

