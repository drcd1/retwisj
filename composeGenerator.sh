#!/bin/bash
# Generates a docker-compose.yml

let port=8080

echo "version: '3'">docker-compose.yml
echo "services:">>docker-compose.yml

echo "">registry/acl.txt
echo "">registry/ret.txt


 

for ((i = 1; i <= $1; i++));
do
	acl_links=""
	ret_links=""
	
	
	if [ ! $1 -eq $i ]
	then
		for ((j = 1; j <= ($1-1); j++));
		do
			if [ ! $j -eq $i ]
			then
				acl_links=$acl_links"acl-"$j":"	
				ret_links=$ret_links"ret-"$j":"
			fi
		done
		
		acl_links=$acl_links"acl-"$1
		ret_links=$ret_links"ret-"$1
	else 
		for ((j = 1; j <= ($1-2); j++));
		do
			acl_links=$acl_links"acl-"$j":"
			ret_links=$ret_links"ret-"$j":"			
		done
			
		acl_links=$acl_links"acl-"$(($1-1))
		ret_links=$ret_links"ret-"$(($1-1))
	fi
	
	
	
	
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
	
	echo "    environment:                 ">>docker-compose.yml	
	echo "      - ACL_LINKS="$acl_links>>docker-compose.yml
	echo "      - MY_NAME=acl-"$i>>docker-compose.yml
	
	
	
	
	
	
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
	echo "    environment:                 ">>docker-compose.yml	
	echo "      - RET_LINKS="$ret_links>>docker-compose.yml
	echo "      - MY_NAME=acl-"$i>>docker-compose.yml
 

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

