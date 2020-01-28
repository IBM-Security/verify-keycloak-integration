# ci-keycloak Dev Cheatsheet

## Build and Access
### Build Image with JAR
(from root) `mvn clean install`

### Keycloak Console
[Admin Console](http://localhost:8080/auth/)

## Operational (from scripts/)
Run Dev Image: 	`./start.sh`
Stop Dev Image:	`./stop.sh`
Delete Data: 	`./removeVolumes.sh`
Remove Image:	`./removeImage.sh`
Hot Swap Jar:	`./copyJar.sh`
