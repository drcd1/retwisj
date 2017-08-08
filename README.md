# Retwisj+ACL
## What is this project?

This is a modification on Retwisj, the java implentation of the twitter clone, Retwis, 
which served as a demo for Spring Data Redis. (LINK)

Our changes subdivide the aplication in two microsservices, Retwisj, which maintains the previous functionality, 
and an Access Control List (ACL), which allows users to block other users. We've also added the ability 
for every microsservice to have multiple replicas, and every change to a replica is propagated across all of them.

The goal of this project is to better allow studying consistency when the system is composed of several microsservices... 
(incomplete...)

## Running Retwisj+ACL

#### Requirements

* A Docker installation

#### How to Run ?

1. Run the script `composeGenereator.sh`. Can be provided with a list of arguments, which are the names of the replicas
to be generated. If run without arguments, it will default to generating to replicas("US" and "EU").
1. Run the command `docker-compose up --build`.
1. Using a browser, open the ports corresponding to any of the Retwisj replicas.


## Retwisj
Although the functionallity was mostly kept from the original (see here: LINK), it was necessary to make some changes:
The Web controller now has two extra methods: block and unblock, for blocking and unblocking users.
Because of the added functionality, the views had to be changed: It was added a button when the user is 
viewing another user's page, for blocking and unblocking users.

## ACL

The ACL structure was kept similar to the structure that already existed in Retwisj. The main class, ACL, 
maintains a connection to Redis (using Spring Data Redis, like Retwisj), where it stores the following information: 

Key | Value
----|---------
{uid}:blocks | List of uids of users blocked by user whose identifier is {uid}
{uid}:blockedBy| List of uids of users who block the user whose identifier is {uid}

ACL also contains methods for writing/reading in the database, functioning like RetwisRepository in Retwisj.

## Comunication between Retwisj and ACL

#### Retwisj
It was added an abstract class, ACLInterface, which allows Retwisj to make requests to the ACL. It's implentations 
(ACLInterfaceRest, ACLInterfaceGrpc and ACLInterfaceThrift), allow the communication between Retwisj and ACL
to be done using REST, gRPC or Apache Thrift.

#### ACL
To allow connection form Retwisj, ACL must launch a Server for every communicatin type. Because ACL uses Spring-boot,
Spring-boot launches the RestController, which acts as a Server for the REST client in Retwisj. The other two Servers 
(for the gRPC and Thrift clients in Retwisj) are launched by ACLServerLauncher. Each server calls methods 
in ACL that ensures the requirements of the client

## Replication

...TO DO...





