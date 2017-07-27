#!/bin/bash
# Generates a docker-compose.yml

echo "version: '3'">docker-compose.yml
echo "services:">>docker-compose.yml

echo "">registry/replicas.java

echo "  retwisj:          ">>docker-compose.yml
echo "    build: ./retwisj">>docker-compose.yml
echo "    ports:          ">>docker-compose.yml
echo "      - '8080:8080' ">>docker-compose.yml
echo "    links:          ">>docker-compose.yml
echo "      - redis       ">>docker-compose.yml
echo "      - acl-2:acl   ">>docker-compose.yml
 

echo "  redis:        ">>docker-compose.yml
echo "    image: redis">>docker-compose.yml
echo "    expose:     ">>docker-compose.yml
echo "      - '6379'  ">>docker-compose.yml
 

for ((i = 1; i <= $1; i++));
do
	echo "acl-"$i>>registry/acl.txt

	echo "  acl-"$i":             ">>docker-compose.yml
	echo "    build: ./acl        ">>docker-compose.yml
	echo "    links:              ">>docker-compose.yml
	echo "     - redis-"$i":redis2">>docker-compose.yml
	echo "    expose:             ">>docker-compose.yml
	echo "     - '8080'           ">>docker-compose.yml
	echo "     - '8084'           ">>docker-compose.yml
	echo "     - '9090'           ">>docker-compose.yml
	
	echo "  redis-"$i":           ">>docker-compose.yml
	echo "    image: redis        ">>docker-compose.yml
	echo "    expose:             ">>docker-compose.yml
	echo "      - '6379'          ">>docker-compose.yml

	
	echo "">>docker-compose.yml
done                 

echo "">>docker-compose.yml
echo "  registry:">>docker-compose.yml
echo "    build: ./registry">>docker-compose.yml
echo "    expose:">>docker-compose.yml
echo "      - '5050'">>docker-compose.yml

