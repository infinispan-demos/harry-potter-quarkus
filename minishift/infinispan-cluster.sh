#!/usr/bin/env bash

# Use MiniShit OpenShift Client
eval $(minishift oc-env)

# You have to be Administrator to install the Operator
oc login -u system:admin

# Install Infinispan Operator running these 3 commands
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/rbac.yaml
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/operator.yaml
oc apply -f https://raw.githubusercontent.com/infinispan/infinispan-operator/master/deploy/crd.yaml

# Create an Infinispan Cluster
oc apply -f infinispan-cluster.yaml