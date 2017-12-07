#!/bin/sh
java -Xmx1g -Xbootclasspath/p:./bin/alpn-boot.jar -cp ./bin/orders-grpc-client-1.0-SNAPSHOT.jar org.gangel.orders.grpc.OrdersGRpcClientApp -j $* -t 1 -i 1000 -wi 5 -h ${TEST_SERVER_HOST} -p 6565 -cert ${TEST_CERT_FILE}
