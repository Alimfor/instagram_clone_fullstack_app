cd ./discovery-service
./run.sh &
cd ../auth-service
./run.sh &
cd ../media-service
./run.sh &
cd ../post-service
./run.sh &
cd ../graph-service
./run.sh &
cd ../feed-service
./run.sh &
cd ../api-gateway
./run.sh