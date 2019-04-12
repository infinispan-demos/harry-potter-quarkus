#!/usr/bin/env bash
oc delete imagestreams wizards-magic
oc delete route wizards-magic
oc delete service wizards-magic
oc delete dc wizards-magic

oc delete imagestreams hogwarts-monitoring
oc delete route hogwarts-monitoring
oc delete service hogwarts-monitoring
oc delete dc hogwarts-monitoring