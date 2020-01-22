# Cloud Identity - Keycloak Integration HLD

## Table of Contents

<details>
 <summary>Click to open</summary>
<!-- MarkdownTOC levels="2,3,4" autolink="true" indent="    " markdown_preview="github" -->

- [1. Overview](#1-overview)
- [2. Traceability](#2-traceability)
- [3. Requirements](#3-requirements)
- [4. Assumptions and Dependencies](#4-assumptions-and-dependencies)
	- [4.1. Assumptions](#41-assumptions)
	- [4.2. Dependencies](#42-dependencies)
- [5. Scenarios/User Stories](#5-scenariosuser-stories)
	- [5.1. Personas](#51-personas)
	- [5.2. Scenarios](#52-scenarios)
- [6. Design Details](#6-design-details)
    - [6.1. Architecture](#61-architecture)
    - [6.2. Data Model](#62-data-model)
		- [6.2.1. Data stored within Cloud Identity](#621-data-stored-within-cloud-identity)
		- [6.2.2. Data stored within Keycloak](#622-data-stored-within-keycloak)
    - [6.3. Multi-tenancy](#63-multi-tenancy)
    - [6.4. Management Interfaces](#64-management-interfaces)
    - [6.5. User Interfaces](#65-user-interfaces)
    - [6.6. Deprecated Interfaces](#66-deprecated-interfaces)
    - [6.7. Audit Logging](#67-audit-logging)
    - [6.8. Reporting](#68-reporting)
    - [6.9. Security Considerations](#69-security-considerations)
    - [6.10. Integration](#610-integration)
    - [6.11. PII/GDPR](#611-piigdpr)
    - [6.12. Migration](#612-migration)
    - [6.13. Future Considerations](#613-future-considerations)
    - [6.14. Discarded Technical Options](#614-discarded-technical-options)
- [7. Operational Impacts](#7-operational-impacts)
    - [7.1. Deployment](#71-deployment)
    - [7.2. Automation](#72-automation)
    - [7.3. Security](#73-security)
    - [7.4. Monitoring](#74-monitoring)
    - [7.5. High Availability](#75-high-availability)
    - [7.6. Disaster Recovery](#76-disaster-recovery)
    - [7.7. Performance and Stability](#77-performance-and-stability)
    - [7.8. Other](#78-other)
- [8. CI/CD Pipeline and Testing](#8-cicd-pipeline-and-testing)
- [9. Documentation](#9-documentation)

<!-- /MarkdownTOC -->
</details>

## 1. Overview

This document will outline and describe the nature and design of how IBM Cloud Identity can be leveraged to provide advanced authentication and authorization features for RedHat SSO (Keycloak) deployments.

Keycloak provides a first-class extension mechanism where custom code can be integrated into an authentication flow, allowing developers to gain control of an end user's authentication context, providing a way to integrate third party services to extend authentication capabilities. This document will describe how we will build such extensions, describing the APIs we will leverage from IBM Cloud Identity.

## 2. Traceability

[GitHub Repo](https://github.com/IBM-Security/cloud-identity-keycloak-integration)

[Aha! Epic](https://ibmsecurity.aha.io/features/CIC-986)

## 3. Requirements

1. Consumable artifacts (JAR files) must be publicly accessible for Alice to consume

## 4. Assumptions and Dependencies

### 4.1 Assumptions

1. A Keycloak deployment is independently acquired/setup.
2. A Cloud Identity tenant is sourced with a CIV subscription (subscriptions are likely going away, so this may become irrelevant)

### 4.2 Dependencies

1. Alice must configure at least 1 API Client with proper entitlements depending on which integrations she wants to leverage.

## 5. Scenarios/User Stories

> These will be further enumerated based on the Aha! epic.

### 5.1 Personas

Standard Cloud Identity personas are in play here:

* **Alice** - Keycloak (and subsequently Cloud Identity) developer. She consumes and integrates the plug-in extensions described in this document to integrate features from Cloud Identity into Keycloak.

* **Jessica** - End user of a customer's Keycloak deployment. Jessica will benefit from the CI - Keycloak integration by being able to use additional authentication factors to access her Keycloak account.

### 5.2 Scenarios

### 5.2.1 As a keycloak developer (Alice), I want to be able to download Cloud Identity Extensions to drop into my Keycloak Deployment

### 5.2.2 ...

## 6. Design Details

### 6.1. Architecture

![Architecture](./images/architecture.png)

* **RedHat SSO (Keycloak)** - The existing Keycloak deployment represents the set of users, applications, and authentication flows that are to be enhanced by integrating with IBM Cloud Identity. Once the integration is complete, these users will be enabled to leverage additional authentication mechanisms when accessing their applications.

* **Cloud Identity** - The IBM Cloud Identity tenant is the instance of Cloud Identity that will be linked to the existing Keycloak deployment. The Cloud Identity tenant is what will perform the various authentication actions based on us driving various API calls. There is various configuration required on the Cloud Identity side when integrating with Keycloak, some of which depending on which authentication mechanisms you are looking to leverage. This will be covered in greater detail below.

* **CI Custom Authenticator** - One or many custom authenticator modules published by this team. Which integrations that are included in each authenticator will be covered in another section. These authenticators are "dropped in" to the Keycloak deployment, making them available for consumption when Alice or another Keycloak admin is setting up authentication flows for their protected applications.

* **IBM Verify Mobile App** - The IBM Verify mobile application is used by the end users of the Keycloak deployment when exercising certain authentication flows provided by IBM Cloud Identity, such as QR Code authentication. Note that it is also possible to leverage a custom mobile application that is implemented via the IBM Verify SDK, instead of directly using the IBM Verify app itself. That is up to the developers implementing the integration(s).

### 6.2. Data Model

There are a few pieces of data persisted by these custom authenticators, in various locations:

#### 6.2.1. Data stored within Cloud Identity

For some of the planned integrations, it is required to persist some data within Cloud Identity. This data includes:

* **User entries** - Some of the authentication factor registrations (FIDO, IBM Verify) require a user record within Cloud Identity for proper identity association. Keycloak users that register with FIDO or IBM Verify will have a corresponding user account created in Cloud Identity for linking purposes.

* **IBM Verify Registrations** - As mentioned above, registering an IBM Verify app for a given user requires a registration resource be created, through public Cloud Identity APIs.

* **FIDO Registrations** - As mentioned above, registering a FIDO device for a given user requires a registration resource be created, through public Cloud Identity APIs.

#### 6.2.2. Data stored within Keycloak

As mentioned in the above section, certain integrations require a user account be created within Cloud Identity's directory that corresponds to a user in the Keycloak directory. Once that user account is created within Cloud Identity, the externally available unique ID for that account will be stored as a custom attribute on the corresponding Keycloak user directory entry. The proposed key for this custom attribute is `cloudIdentityUserId`, but we should also consider exposing this as a configurable option within the custom authenticator modules, allowing Alice to specify what the attribute should be named.

We can also consider alternative linking strategies between CI and Keycloak user accounts, potentially offering Alice a mechanism outside of custom user attributes. If such a need is identified, we can explore it further. The custom user attribute is just the first functional approach identified.

### 6.3. Multi-tenancy

Not relevant to this design, as the product of this design does not run within Cloud Identity, but rather is a consumer of Cloud Identity.

### 6.4. Management Interfaces

Each custom authenticator built by this project will expose a certain set of configuration criteria, which Alice will be required to supply. These configurations are provided through the Keycloak administrator console, when the custom authenticators are added to an authentication flow. These configurations include but are not limited to:

* **Cloud Identity Tenant FQDN** - The fully-qualified domain name (FQDN) of the Cloud Identity tenant Alice intends to integrate with. This is used to properly route API calls from the custom authenticators.

* **API Client ID** - The Client ID of the OAuth-based API Client, as provided by Cloud Identity. It is up to Alice to create and properly entitle an API Client using the Cloud Identity Adminstrator UI. Entitlement requirements will be called out specifically for each integration point elsewhere in this document.

* **API Client Secret** - The Client Secret of the OAuth-based API Client, as provided by Cloud Identity. See the above description of API Client ID for more information.

### 6.5. User Interfaces

This project will include User Interfaces that are invoked in runtime authentication flows by Jessica, the end user. These UIs will expose the various authentication methods/factors that Jessica can configure/use.

Keycloak custom authenticators use the [FreeMarker Template Engine](https://freemarker.apache.org/) to process and render all UIs that are part of the authentication process. The expectation is that we will hook into this existing system to build our UIs, though this does not necessarily dictate any specific client-side technology. There is still some research to be done to determine how we could potentially pull in external dependencies for client-side UI technology, integrating them into the FreeMarker template pages.

We will be responsible for building UIs that expose the ability for Jessica to:

1. Register an IBM Verify application on her mobile device

2. Register a FIDO/WebAuthn security device

3. Perform First Factor authentication by scanning a QR code with her IBM Verify application

4. Perform First Factor authentication via a FIDO/WebAuthn device

Other UIs will be built as the user stories are identified and prioritized.

The current plan is to implement these UIs following the visual design patterns currently in place by Keycloak, out of the box. However, we should also expose a mechanism for Alice to supply her own template files to replace ours, giving Alice the ability to customize the user experience her users encounter, as it is likely that she has already performed similar actions on the pre-existing template pages to provide proper branding.

### 6.6. Deprecated Interfaces

The intent of this project is not to provide programmatic interfaces that Alice will consume (though it could be extended to do that, if we discover such a need). Therefore, there is not really a threat of deprecating external interfaces.

However, it should be noted that a majority of the functionality that will be packaged in these custom authenticators is dependent upon SPIs provided by Keycloak that are subject to change with newer Keycloak releases. That means we will need to identify, which each released artifact, a set of compatible Keycloak versions.

These extensions are also dependent upon Cloud Identity public APIs. As these APIs evolve and are deprecated/removed from service, the extensions will need to be kept in sync. This is likely to be less of an issue compared to Keycloak's SPIs given the public nature of the CI APIs and that contractually Cloud Identity is expected to support public APIs for a given period of time.

### 6.7. Audit Logging

This is still TBD, but we will need to figure out what events from the runtime flow are interesting/audit-worthy, and establish a pattern of logging/tracing for proper code support in production environments.

### 6.8. Reporting

N/A

### 6.9. Security Considerations

Proper session management and timeouts are required during the authentication flow. In addition, cleanup of authentication transactions through Cloud Identity APIs should be performed.

### 6.10. Integration

This whole project is an integration. I won't go into detail here.

### 6.11. PII/GDPR

This project stores data in Keycloak and CI, so PII/GDPR concerns will be addressed by those products.

### 6.12. Migration

As this is a new project, we do not yet have any migration concerns. As we iteratively release new versions/artifacts, we will address this concern as needed.

### 6.13. Future Considerations

TBD

### 6.14. Discarded Technical Options

N/A

## 7. Operational Impacts

### 7.1. Deployment

Deployment of these custom authenticators is delegated to Alice, as the custom authenticators will run within a pre-existing Keycloak deployment. As new releases are built, any kind of data migration requirements will be considered at that time.

### 7.2. Automation

Out of scope.

### 7.3. Security

All data storage is handled by Keycloak and Cloud Identity. These extensions themselves are just drop-ins that add functionality. Data security is therefore inherited from Keycloak and Cloud Identity.

### 7.4. Monitoring

As of now, monitoring of these custom authenticators will be handled by log traversal within Keycloak deployments. If there is any kind of monitoring/health check SPIs made available by Keycloak, we will integrate with them, as needed.

### 7.5. High Availability

HA of Keycloak deployments is out of scope for us, and up to Alice. HA of Cloud Identity resources is also out of scope of this effort, and up to Cloud Identity itself.

### 7.6. Disaster Recovery

DR of Keycloak deployments is out of scope for us, and up to Alice. DR of Cloud Identity resources is also out of scope of this effort, and up to Cloud Identity itself.

### 7.7. Performance and Stability

Custom authenticator performance is dependent upon the following factors:
* Performance of the various Cloud Identity APIs leveraged by a given custom authenticator
* Optimization of Cloud Identity API requests made by custom authenticators (caching access tokens instead of requesting fresh ones per flow, etc.)
* Optimization of interaction with Keycloak user directory when user lookups are required, especially based on custom attributes (potential need for custom DB indices based on user lookup approaches)

### 7.8. Other

None at the moment.

## 8. CI/CD Pipeline and Testing

This project will follow the existing patterns for Cloud Identity development, testing, continuous integration, and continuous release cycles.

Unit tests will be written for individual functions/features within the development code.

Integration tests will be performed by setting up testing environments with various Keycloak deployments (for whichever version(s) we intend to support), in both _new_ deployments and _existing_ deployments. There will also be a set of Cloud Identity tenants (from the PRODUCTION environment) made available to continuously test integrations between Keycloak deployments and Cloud Identity subscriptions. Given these environments, tests will be written to recreate both Alice and Jessica flows surrounding the custom authenticators being built.

There will be daily test runs executed against these various test environments, verifying continued support of known features/functions. Additionally, as active development continues, new tests will be written to verify newly developed features. These new features and tests will be executed by the _development_ build/test pipelines.

All of this will be orchestrated via the existing Jenkins instance setup within the Cloud Identity development organization.

Build/test/release pipelines will be setup to support automating development and release builds, which involves testing the produced artifacts. Once a stable, release-ready artifact is produced, the release portion of the pipeline will be executed. As described earlier, the artifacts produced by this project will be bundled JAR files that can be dropped into Alice's Keycloak deployment. The release portion of the pipeline will then be responsible for publishing the artifact(s) to a publicly-accessible artifact repository. Specifics are still TBD, but something like [Maven Central](https://search.maven.org/) is the leading candidate.

## 9. Documentation

Extension documentation will be housed at the [GitHub repository](https://github.com/IBM-Security/cloud-identity-keycloak-integration) using various `markdown` files.

Documentation should include but not be limited to:
* Enumeration and explanation of available custom authenticators
* API Client entitlement requirements for each available custom authenticator
* Configuration options for each available custom authenticator
* Integration/setup steps, guiding Alice through using the Keycloak admin console to integrate one of more custom authenticators
* Walkthrough of sample integration(s)


# DOCUMENT PROPERTIES

**Authors**: Austin Bruch (afbruch@us.ibm.com)

**Status**: DRAFT

**Review PR**: [GitHub PR](https://github.com/IBM-Security/cloud-identity-keycloak-integration/pull/3)
