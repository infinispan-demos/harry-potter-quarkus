#!/usr/bin/env bash

set -e -x

minishift profile set harry-potter-quarkus
minishift config set memory 8GB
minishift config set cpus 6
minishift config set disk-size 100g
minishift config set image-caching true
minishift config set vm-driver virtualbox

# Enable admin-user to delete projects.
minishift addon enable admin-user
