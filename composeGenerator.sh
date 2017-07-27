#!/bin/bash
# Generates a docker-compose.yml

let port=8080

echo "version: '3'">docker-compose.yml
echo "services:">>docker-compose.yml

echo "">registry/acl.txt
echo "">registry/ret.txt


 

for ((i = 1; i <= $1; i++));
do
	echo "acl-"$i>>registry/acl.txt
	echo "retwisj-"$i>>registry/ret.txt

	echo "  acl-"$i":                 ">>docker-compose.yml
	echo "    build: ./acl            ">>docker-compose.yml
	echo "    links:                  ">>docker-compose.yml
	echo "     - redis-acl-"$i":redis2">>docker-compose.yml
	echo "    expose:                 ">>docker-compose.yml
	echo "     - '8080'               ">>docker-compose.yml
	echo "     - '8084'               ">>docker-compose.yml
	echo "     - '9090'               ">>docker-compose.yml
	
	echo "  redis-acl-"$i":           ">>docker-compose.yml
	echo "    image: redis            ">>docker-compose.yml
	echo "    expose:                 ">>docker-compose.yml
	echo "      - '6379'              ">>docker-compose.yml
	
	echo "  retwisj-"$i":                   ">>docker-compose.yml
	echo "    build: ./retwisj         ">>docker-compose.yml
	echo "    ports:                   ">>docker-compose.yml
	echo "      - '"$((port++))":8080'  ">>docker-compose.yml
	echo "    links:                   ">>docker-compose.yml
	echo "      - redis-ret-"$i":redis ">>docker-compose.yml
	echo "      - acl-"$i":acl         ">>docker-compose.yml
 

	echo "  redis-ret-"$i":">>docker-compose.yml
	echo "    image: redis ">>docker-compose.yml
	echo "    expose:      ">>docker-compose.yml
	echo "      - '6379'   ">>docker-compose.yml

	
	echo "">>docker-compose.yml
done                 

echo "">>docker-compose.yml
echo "  registry:">>docker-compose.yml
echo "    build: ./registry">>docker-compose.yml
echo "    expose:">>docker-compose.yml
echo "      - '5050'">>docker-compose.yml

