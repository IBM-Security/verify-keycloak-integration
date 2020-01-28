#!/bin/bash
# Run after containers are stopped to remove Volumes (data)
docker-compose -f ../ci-keycloak.yml down -v
