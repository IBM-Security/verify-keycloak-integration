#!/bin/bash
docker rmi -f $(docker image ls verify-keycloak -q)
