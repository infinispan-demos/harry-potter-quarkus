# Quarkus demo: Infinispan Client

This example showcases how to use Infinispan client with Quarkus.
* Create stores dynamically
* Simple get/put operations
* Full-text query
* Continuous query

Uses Quarkus features, such as
* REST web-service
* Web Socket
* Task Scheduling
* Application properties
* Application lifecycle events listeners

# Before running the demo

Quarkus uses [Infinispan Remote Client](http://infinispan.org/docs/dev/user_guide/user_guide.html#client_server).
You need to have an Infinispan Server running before you run this demo.

Option 1: Use Docker `docker run -it -p 11222:11222 jboss/infinispan-server:latest`
Option 2: Download and run the server
- Download the server from [here](http://downloads.jboss.org/infinispan/10.0.0.Beta2/infinispan-server-10.0.0.Beta2.zip)
- Unzip the file 
- Run a standalone server from the installation folder: `./bin/standalone.sh`

# Harry-Potter demo

The demo will load characters and spells. Characters can execute spells, and we want to monitor who is in Hogwarts and 
performing magic.

- A `Character` has an id, name, biography and type (other, student, teacher, muggle).
- A `Spell` has an id, name and description.
- on startup timeA `Spell` is performed by a character. This character may or may not be at Hogwarts.

## Data loading

`DataLoader` class loads characters and spells in two separate stores.
This is done at startup time

## Magic is going on
[Hogwarts Magic Creator](src/main/java/org/acme/infinispanclient/service/HogwartsMagicCreator.java) is going to emulate
characters performing some magic. It will randomly pick a character and a spell to perform (if they can)!
Characters in Hogwarts are teachers or students. 

## Search
[A simple REST service](src/main/java/org/acme/infinispanclient/CharactersResource.java) is available to query
characters by id or perform a full-text search on top of the name or the biography. 

## Magic Socket
[A socket](src/main/java/org/acme/infinispanclient/HogwartsMagicSocket.java) that performs a Continuous Query making it possible to
monitor which characters in Hogwarts are currently performing magic.

# Run the demo in dev mode
To run in development mode, just run `mvn clean compile quarkus:dev`. This allows for hot swapping the application, so you can tweak parts of it 
and it will automatically redeploy.
Go to `http://localhost:8081` and monitor the magic!

You will be connected to the monitoring socket. The interface displays some links to display a character by id and
 perform a full-text search.

### Tip for docker for mac users
If you are using docker for mac there is a known issue to connect with the server using the hotrod client.
Blog post [here](https://blog.infinispan.org/2018/03/accessing-infinispan-inside-docker-for.html]).

You won't need to do this in production, but for testing/development purposes, if you want to connect to a Infinispan 
Server running in a Docker container:

1) Create `hotrod-client.properties` file under `resources/META-INF` folder
2) Configure `infinispan.client.hotrod.client_intelligence=BASIC`

# Run the demo with jar

- `mvn clean package`
- `cd target`
- `java -jar  -Dcharacters.filename=classes/hp_characters.csv -Dspells.filename=classes/hp_spells.csv harry-potter-quarkus-runner.jar`

The data that is loaded is copied under classes folder in target. We can override these file names in runtime. 
Those properties are configured in the application.properties and injected to the `DataLoader` service.

# Run the demo in native mode
Compile the application in native mode:

- `mvn package -Pnative`
- `cd target`
- `./harry-potter-quarkus-runner -Dcharacters.filename=classes/hp_characters.csv -Dspells.filename=classes/hp_spells.csv`

The data that is loaded is copied under classes folder in target. We can override these file names in runtime. 
Those properties are configured in the application.properties and injected to the `DataLoader` service.

# Run and deploying in Openshift
`mvn package -Pnative -Dnative-image.docker-build=true`
TBD


