# verify-keycloak-integration

This repository contains development of a set of authenticator extensions for enhancing authentication capabilities with Keycloak and/or RedHat SSO with IBM Security Verify.

## Setting up a development environment
### Software Requirements
* IBM JRE 1.8
* Docker
* Maven

### Build
`mvn clean install` from the root directory - creates a ci-keycloak Docker image

### Deployment Scripts
In the <root_dir>/dev/scripts folder:
* `start.sh` : starts the Keycloak instance with built extensions
* `stop.sh` : stops the Keycloak server
* `removeImage.sh` : remove existing Keycloak extensions image
* `removeVolumes.sh` : remove all Keycloak instance data
* `copyJars.sh` : copies newly built extensions JAR into running Keycloak instance

This code is [Apache 2.0 licensed](./LICENSE).
