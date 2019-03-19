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

### Option 1: Use Docker 
`docker run -it -p 11222:11222 jboss/infinispan-server:latest`

### Option 2: Download and run the server
- Download the server from [here](http://downloads.jboss.org/infinispan/10.0.0.Beta2/infinispan-server-10.0.0.Beta2.zip)
- Unzip the file 
- Run a standalone server from the installation folder: `./bin/standalone.sh`

# Harry-Potter demo

The demo will load characters and spells. Characters can execute spells, and we want to monitor who is in Hogwarts and 
performing magic.

- A `Character` has an id, name, biography and type (other, student, teacher, muggle).
- A `Spell` has an id, name and description.
- A `Spell` is performed by a character. This character may or may not be at Hogwarts.

## Data loading

`DataLoader` class loads characters and spells in two separate stores.
This is done at startup time

## Magic is going on
[Hogwarts Magic Creator](src/main/java/org/infinispan/hp/service/HogwartsMagicCreator.java) is going to emulate
characters performing some magic. It will randomly pick a character and a spell to perform (if they can)!
Characters in Hogwarts are teachers or students. 

## Search
[A simple REST service](src/main/java/org/infinispan/hp/CharactersResource.java) is available to query
characters by id or perform a full-text search of the name or the biography. 

## Magic Socket
[A socket](src/main/java/org/infinispan/hp/HogwartsMagicWebSocket.java) that performs a Continuous Query making it possible to
monitor which characters in Hogwarts are currently performing magic.
The continuous query does not have to go to the server and it is all stored in the client application itself. [Read more 
about these queries in the official Infinispan documentation](http://infinispan.org/docs/stable/user_guide/user_guide.html#query_continuous).

# Run the demo in dev mode
To run in development mode, just run `mvn clean compile quarkus:dev`. This allows for hot swapping the application, so you can tweak parts of it 
and it will automatically redeploy.
Go to `http://localhost:8081` and monitor the magic!

You will be connected to the monitoring socket. The interface displays some links to display a character by id and
 perform a full-text search.

### Tip for docker for mac users
If you are using docker for mac there is a known issue to connect with the server using the hotrod client.
Blog post [here](https://blog.infinispan.org/2018/03/accessing-infinispan-inside-docker-for.html).

You won't need to do this in production, but for testing/development purposes, if you want to connect to a Infinispan 
Server running in a Docker container:

1) Create `hotrod-client.properties` file under `resources/META-INF` folder
2) Configure `infinispan.client.hotrod.client_intelligence=BASIC`

# Run the demo with jar

- `mvn clean package`
- `cd target`
- `java -jar harry-potter-quarkus-runner.jar`

# Run the demo in native mode
Compile the application in native mode:

- `mvn package -Pnative`
- `cd target`
- `./harry-potter-quarkus-runner`

### Loaded data 
Maven copies `hp_characters.csv` and `hp_spells.csv`to the target directory, that's why it's easier to run the executables
from the `target` folder. However you can override these files location at runtime.

- Running the jar
 
   `java -jar  -Dcharacters.filename=/my/path/hp_characters.csv -Dspells.filename=/my/path/hp_spells.csv harry-potter-quarkus-runner.jar`

- Running the native
 
   `./harry-potter-quarkus-runner -Dcharacters.filename=/my/path/hp_characters.csv -Dspells.filename=/my/path/hp_spells.csv`


# Run and deploying in Openshift
`mvn package -Pnative -Dnative-image.docker-build=true`
TBD


