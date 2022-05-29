#!/bin/sh

set -v

echo "Place new order"
ORDER_ID=$(curl -s -X POST http://localhost:8080/orders/ -d '{"riderId":"63770803-38f4-4594-aec2-4c74918f7165","price":"123.45","route":[{"address":"Kyiv, 17A Polyarna Street","lat":50.51980052414157,"lon":30.467197278948536},{"address":"Kyiv, 18V Novokostyantynivska Street","lat":50.48509161169076,"lon":30.485170724431292}]}' -H 'Content-Type: application/json' | jq -r .orderId)
sleep 2s

echo "Get the placed order"
curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq

echo "Accept the order"
curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"ACCEPTED","driverId":"2c068a1a-9263-433f-a70b-067d51b98378","version":1}' -H 'Content-Type: application/json'
sleep 2s

echo "Get the accepted order"
curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq

echo "Try to cancel an outdated version of the order to simulate lost update"
curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"CANCELLED","version":1}' -H 'Content-Type: application/json' | jq

echo "Try to cancel a version of the order 'from the future' to simulate unordering"
curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"CANCELLED","version":3}' -H 'Content-Type: application/json' | jq

echo "Complete the order"
curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"COMPLETED","version":2}' -H 'Content-Type: application/json'
sleep 2s

echo "Get the completed order"
curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq

echo "Try to cancel a completed order to simulate business rule violation"
curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"CANCELLED","version":3}' -H 'Content-Type: application/json' | jq

echo "Print integration events"
docker compose exec kafka /bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic order-integration-events --from-beginning --property print.key=true --timeout-ms 10000