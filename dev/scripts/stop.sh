#!/bin/bash
docker stop $(docker ps | grep dev_keycloak_1 | cut -d' ' -f1) &&
docker stop $(docker ps | grep dev_postgres_1 | cut -d' ' -f1)
