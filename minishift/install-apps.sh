#!/usr/bin/env bash
oc import-image karesti/wizards-magic --confirm
oc new-app wizards-magic --name wizards-magic
oc expose service wizards-magic

oc import-image karesti/hogwarts-monitoring --confirm
oc new-app hogwarts-monitoring --name hogwarts-monitoring
oc expose service hogwarts-monitoring