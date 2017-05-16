#/bin/sh
PORT=$1
URI=$2

echo "POSTING SupplierManagement.scr for PartnershipSuppliers and projecting authorisersvc"
echo -n ?; read x
curl -i --data "@SupplierManagement.scr" http://localhost:${PORT}${URI}/____eppLoad____PartnershipSuppliers____authorisersvc
echo -n ?; read x
echo "POSTING accept_connection__from_requestor"
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____accept_connection__from_requestor
echo -n ?; read x
echo "POSTING receiveMessage: '{"function":"getsuppliers","from":"requestor","parameters":{"param1":{"uuid":"991"}}}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"getsuppliers","from":"requestor","parameters":{"param1":{"uuid":"991"}}}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x
echo "POSTING request_connection__to_suppliersvc"
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_connection__to_suppliersvc
echo -n ?; read x
echo "POSTING sendMessage: '{"function":"getsuppliers","to":"suppliersvc"}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"getsuppliers","to":"suppliersvc"}' http://localhost:${PORT}${URI}/____sendMessage
echo -n ?; read x
echo "POSTING receiveMessage: '{"function":"suppliers","from":"suppliersvc"}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"suppliers","from":"suppliersvc"}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x
echo "POSTING request_connection__to_filtersvc"
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_connection__to_filtersvc
echo -n ?; read x
echo "POSTING sendMessage: '{"function":"filterSuppliers","to":"filtersvc","parameters":{"param1":{"usercontext":"somecontext"},"param2":{"filters":"somefiler"},"param3":{"supplierdetails":"somesupplierdetails"}}}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"filterSuppliers","to":"filtersvc","parameters":{"param1":{"usercontext":"somecontext"},"param2":{"filters":"somefiler"},"param3":{"supplierdetails":"somesupplierdetails"}}}' http://localhost:${PORT}${URI}/____sendMessage
echo -n ?; read x
echo "POSTING receiveMessage: '{"function":"filtered","from":"filtersvc"}'"
curl -H "Content-Type: application/json" -X POST -d '{"function":"filtered","from":"filtersvc"}' http://localhost:${PORT}${URI}/____receiveMessage
echo -n ?; read x
echo "POSTING request_disconnection__from_filtersvc"
curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_disconnection__from_filtersvc
echo -n ?; read x

curl -H "Content-Type: application/json" -X POST http://localhost:${PORT}${URI}/____request_disconnection__from_suppliersvc
echo -n ?; read x
curl -H "Content-Type: application/json" -X POST -d '{"function":"suppliers","to":"requestor"}' http://localhost:${PORT}${URI}/____sendMessage
