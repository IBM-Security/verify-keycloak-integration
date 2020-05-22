#!/bin/bash
docker cp ../../IBMSecurityVerifyAuthenticators/target/IBMSecurityVerifyAuthenticators-0.0.1.jar $(docker ps | grep dev_keycloak_1 | cut -d' ' -f1):/opt/jboss/keycloak/standalone/deployments
