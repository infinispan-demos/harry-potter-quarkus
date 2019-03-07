# Quarkus demo: Infinispan Client

This example showcases how to use Infinispan client with Quarkus. 

# Run infinispan server

- Running with docker `docker run -it -p 11222:11222 jboss/infinispan-server:latest`
- Download the server from `http://www.infinsispan.org` and run `./bin/standalone.sh`

# Harry-Potter demo

The demo will load characters and spells. Characters can execute spells, and we want to monitor who is in
Hogwarts and performing magic.

A Character has an id, name, biography and type (other, student, teacher, muggle).
A Spell has an id, name and description.
A Spell is performed by a character. This character may or may not be at Hogwarts.

## Data loading
`DataLoader` class loads characters and spells in two separate stores.
This is done on startup time

## Magic is going on
[Hogwarts Magic Creator](src/main/java/org/acme/infinispanclient/service/HogwartsMagicCreator.java) is going to emulate
characters performing some magic. It will randomly pick a character and a spell to perform (if they can)!
Characters in Hogwarts are teachers or students. 

## Search Search
[A simple REST service](src/main/java/org/acme/infinispanclient/CharactersResource.java) is available to query
characters by id or perform a full-text search on top of the name or the biography. 

## Magic Socket
[A socket](src/main/java/org/acme/infinispanclient/HogwartsMagicSocket.java) that performs Continuous Query make possible to
monitor which characters in Hogwarts are now performing some magic.

# Run the demo in dev mode
In dev mode, just run `mvn clean compile quarkus:dev`
Go to `http://localhost:8080` and monitor the magic!
You will be connected to the monitoring socket and you will find there the links to perform some REST search.

# Run the demo 

1) `mvn clean package`

2) `java -jar target/harry-potter-quarkus-runner.jar`

# Native mode and deploying in Openshift
Compile the application in native mode:

`mvn package -Pnative -Dnative-image.docker-build=true`

TBD


