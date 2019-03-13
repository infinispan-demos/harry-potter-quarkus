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


# OpenShift

In this part we will be deploying the application in OpenShift. 
We are going to install Infinispan in OpenShift using Operators. To make this possible you need administration rights.
Operators are supported for OpenShift 4 or 3.11.

## Using MiniShift

### Install MiniShift
A simple way to deploy locally and test, you can use [MiniShift](https://docs.okd.io/latest/minishift/getting-started/installing.html).

Important: This tutorial user **VirtualBox**, but this can be changed in the [setup file](./minishift/setup-minishift.sh)

Once MiniShift is installed

- `./minishift/setup-minishift.sh`
- `minishift start`

You should be able to access to the console

```bash
The server is accessible via web console at:
https://192.168.99.117:8443/console
```

### Install Infinispan Cluster

Run `infinispan-cluster.sh`

This file contains all the necessary commands to install the operator and the Infinispan Cluster.

You can access to OpenShift console

![OpenShift web interface](./minishift/OperatorAndCluster.png)

### Build the application

1) Configure `infinispan-client.server-list` property

The application is going to be deployed in OpenShift, and will connect to the Infinispan Cluster that is available.

Today the `quarkus.infinispan-client.server-list` is a build time property. This means that the current version of
Quarkus does not allow to override the value dynamically. 

Before building the application, change this value so the application will be able to connect to the Infinispan Cluster
once it will be deployed in OpenShift :
`quarkus.infinispan-client.server-list=expecto-patronum-infinispan:11222`

2) Build the application native, but for docker

`mvn clean package -Pnative -Dnative-image.docker-build=true`

### Deploy the application
You have two options.

#### Option 1: Use Docker Registry
An image of the application is available in the public docker hub: 
`karesti/harry-potter-quarkus:tagname`
You can deploy an image from the OpenShift web console. 

#### Option 2: Use OpenShift build

```bash 
oc new-build --binary --name=-oc-harry-potter-quarkus -l app=oc-harry-potter-quarkus
oc start-build oc-harry-potter-quarkus --from-dir=. --follow
oc new-app --image-stream=oc-harry-potter-quarkus:latest
```
You will see that the application is deployed and the logs can be displayed.

#### Access to the application with the browser

If you want to access to the interface on the browser, you need to expose a service.

Run `oc expose service oc-harry-potter-quarkus` (or another app name)





