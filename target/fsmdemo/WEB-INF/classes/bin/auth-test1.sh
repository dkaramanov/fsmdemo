#/bin/sh
PORT=$1
URI=$2

curl -H "Content-Type: application/json" -X POST -d '{"function":"getcontracts","from":"requestor"}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_connection__to_contractsvc
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"getcontracts","to":"contractsvc"}' http://localhost:${PORT}${URI}/____sendMessage
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"contracts","from":"contractsvc"}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_connection__to_filtersvc
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"filterContracts","to":"filtersvc","parameters":{"param1":{"usercontext":"somecontext"},"param2":{"filters":"somefiler"},"param3":{"contractdetails":"somecontractdetails"}}}' http://localhost:${PORT}${URI}/____sendMessage
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"filtered","from":"filtersvc"}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_disconnection__from_filtersvc
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_disconnection__from_contractsvc
echo -n ?; read x 1
curl -H "Content-Type: application/json" -X POST -d '{"function":"contracts","to":"requestor"}' http://localhost:${PORT}${URI}/____sendMessage
