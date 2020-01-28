#!/bin/bash
docker rmi -f $(docker image ls ci-keycloak -q)
