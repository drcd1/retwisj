#!/bin/bash
# Generates a docker-compose.yml

let port=8080

echo "version: '3'">docker-compose.yml
echo "services:">>docker-compose.yml

if [ $# -eq 0 ];
    then
        zone_list=("eu" "us")
    else
        zone_list=( "$@" )
fi

n_zones=${#zone_list[@]}
    
for ((i = 0; i < $n_zones; i++));
do
    acl_links=""
    ret_links=""
    
    
    if [ ! $(($n_zones-1)) -eq $i ]
    then
        for ((j = 0; j < ($n_zones-1); j++));
        do
            if [ ! $j -eq $i ]
            then
                acl_links=$acl_links"acl-"${zone_list[$j]}":"   
                ret_links=$ret_links"retwisj-"${zone_list[$j]}":"
            fi
        done
        
        acl_links=$acl_links"acl-"${zone_list[$n_zones-1]}
        ret_links=$ret_links"retwisj-"${zone_list[$n_zones-1]}
    else 
        for ((j = 0; j < ($n_zones-2); j++));
        do
            acl_links=$acl_links"acl-"${zone_list[$j]}":"
            ret_links=$ret_links"retwisj-"${zone_list[$j]}":"           
        done
            
        acl_links=$acl_links"acl-"${zone_list[$n_zones-2]}
        ret_links=$ret_links"retwisj-"${zone_list[$n_zones-2]}
    fi
    
    
    

    echo "  acl-"${zone_list[$i]}":                 ">>docker-compose.yml
    echo "    build: ./acl                          ">>docker-compose.yml
    echo "    links:                                ">>docker-compose.yml
    echo "     - redis-acl-"${zone_list[$i]}":redis2">>docker-compose.yml
    echo "    expose:                               ">>docker-compose.yml
    echo "     - '8080'                             ">>docker-compose.yml
    echo "     - '8084'                             ">>docker-compose.yml
    echo "     - '9090'                             ">>docker-compose.yml
    
    echo "    environment:                 ">>docker-compose.yml    
    echo "      - ACL_LINKS="$acl_links>>docker-compose.yml
    echo "      - MY_NAME=acl-"${zone_list[$i]}>>docker-compose.yml
    
    
    
    
    
    
    echo "  redis-acl-"${zone_list[$i]}":           ">>docker-compose.yml
    echo "    image: redis            ">>docker-compose.yml
    echo "    expose:                 ">>docker-compose.yml
    echo "      - '6379'              ">>docker-compose.yml
    
    echo "  retwisj-"${zone_list[$i]}":                   ">>docker-compose.yml
    echo "    build: ./retwisj         ">>docker-compose.yml
    echo "    ports:                   ">>docker-compose.yml
    echo "      - '"$((port++))":8080'  ">>docker-compose.yml
    echo "    links:                   ">>docker-compose.yml
    echo "      - redis-ret-"${zone_list[$i]}":redis ">>docker-compose.yml
    echo "      - acl-"${zone_list[$i]}":acl         ">>docker-compose.yml
    echo "    environment:                 ">>docker-compose.yml    
    echo "      - RET_LINKS="$ret_links>>docker-compose.yml
    echo "      - MY_NAME=retwisj-"${zone_list[$i]}>>docker-compose.yml
 

    echo "  redis-ret-"${zone_list[$i]}":">>docker-compose.yml
    echo "    image: redis ">>docker-compose.yml
    echo "    expose:      ">>docker-compose.yml
    echo "      - '6379'   ">>docker-compose.yml

    
    echo "">>docker-compose.yml
done                 

ret_links=""

for ((j = 0; j < ($n_zones-1); j++));
do
  ret_links=$ret_links"retwisj-"${zone_list[$j]}":"  
done
ret_links=$ret_links"retwisj-"${zone_list[$n_zones-1]}

echo "  sandbox:           ">>docker-compose.yml
echo "    build: ./sandbox ">>docker-compose.yml
echo "    tty: true        ">>docker-compose.yml
echo "    environment:       ">>docker-compose.yml
echo "      - RET_LINKS="$ret_links>>docker-compose.yml

