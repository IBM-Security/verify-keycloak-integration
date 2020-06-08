# IBM Security Verify Authenticator Extensions for Keycloak/RedHat SSO

This repository contains development of a set of authenticator extensions for enhancing authentication capabilities with Keycloak and/or RedHat SSO with IBM Security Verify.

## Developer Point of Contact
Jason Choi <jason.choi1@ibm.com>

## Setting up a development environment
### Software Requirements
* IBM JRE 1.8
* Docker
* Maven

### Build
`mvn clean install` from the root directory - creates a ci-keycloak Docker image

The build process creates a Keycloak/RedHat SSO compatible extensions JAR with the IBM Security Verify authenticator extensions. The JAR file is placed into a Keycloak SSO docker image into the `<root_dir>/standalone/deployments` directory and will be deployed automatically when the image is started. Please refer to [Deployment Scripts](#deployment-scripts) for a list of commands that manage the built image.

### Deployment Scripts
In the <root_dir>/dev/scripts folder:
* `start.sh` : starts the Keycloak instance with built extensions
* `stop.sh` : stops the Keycloak server
* `removeImage.sh` : remove existing Keycloak extensions image
* `removeVolumes.sh` : remove all Keycloak instance data
* `copyJars.sh` : copies newly built extensions JAR into running Keycloak instance

### Accessing the Keycloak Instance
By default, the deployed instance can be accessed on [http://localhost:8080](http://localhost:8080)

## Contributing to the Verify Authenticator Extensions Project
### Process
* Create a branch of `master` and name it after the feature you are implementing (e.g. account_attribute_support)
* Follow the [Project Structure & Coding Guidelines](#project-structure--coding-guidelines)
* Create a pull request when feature is complete

### Project Structure & Coding Guidelines
* verify-keycloak
  * src/main/java
      * com.ibm.security.verify.authenticator - Abstract classes
      * com.ibm.security.verify.authenticator.<authenticator_name> - Authenticator specific implementations
      * com.ibm.security.verify.authenticator.rest - Authenticator ReST implementations
      * com.ibm.security.verify.authenticator.utils - Logging
  * src/main/resources
      * META-INF/services - Authenticator registration
      * theme-resources - Localization, images, and templates
* dev - Docker YAML file and utility scripts
* pom.xml - Main Maven build file

### 

## Reporting Issues
Please open a GitHub Issue to report any problems with the authenticator. The repository is actively monitored



This code is [Apache 2.0 licensed](./LICENSE).
