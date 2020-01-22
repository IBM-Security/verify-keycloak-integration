# Cloud Identity - Keycloak Integration HLD

## Table of Contents

<details>
 <summary>Click to open</summary>
<!-- MarkdownTOC levels="2,3,4" autolink="true" indent="    " markdown_preview="github" -->

- [1. Overview](#1-overview)
- [2. Traceability](#2-traceability)
- [3. Requirements](#3-requirements)
- [4. Assumptions and Dependencies](#4-assumptions-and-dependencies)
	- [4.1 Assumptions](#41-assumptions)
	- [4.2 Dependencies](#42-dependencies)
- [5. Scenarios/User Stories](#5-scenariosuser-stories)
- [6. Design Details](#6-design-details)
    - [6.1. Architecture](#61-architecture)
    - [6.2. Data Model](#62-data-model)
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
- [8. Documentation](#8-documentation)
- [9. Testing](#9-testing)

<!-- /MarkdownTOC -->
</details>

## 1. Overview

_Provide an overview of the functionality that is being designed._

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

_Provide an overview of the architecture of the product offering that this document discusses the design for. Include an architecture diagram, and a 4-5 line description of each component in this diagram stating its purpose. Highlight new and updated components for this design and tie it to the  scenarios._

![Architecture](./images/architecture.png)

* **RedHat SSO (Keycloak)** - The existing Keycloak deployment represents the set of users, applications, and authentication flows that are to be enhanced by integrating with IBM Cloud Identity. Once the integration is complete, these users will be enabled to leverage additional authentication mechanisms when accessing their applications.

* **Cloud Identity** - The IBM Cloud Identity tenant is the instance of Cloud Identity that will be linked to the existing Keycloak deployment. The Cloud Identity tenant is what will perform the various authentication actions based on us driving various API calls. There is various configuration required on the Cloud Identity side when integrating with Keycloak, some of which depending on which authentication mechanisms you are looking to leverage. This will be covered in greater detail below.

* **CI Custom Authenticator** - One or many custom authenticator modules published by this team. Which integrations that are included in each authenticator will be covered in another section. These authenticators are "dropped in" to the Keycloak deployment, making them available for consumption when Alice or another Keycloak admin is setting up authentication flows for their protected applications.

* **IBM Verify Mobile App** - The IBM Verify mobile application is used by the end users of the Keycloak deployment when exercising certain authentication flows provided by IBM Cloud Identity, such as QR Code authentication. Note that it is also possible to leverage a custom mobile application that is implemented via the IBM Verify SDK, instead of directly using the IBM Verify app itself. That is up to the developers implementing the integration(s).

### 6.2. Data Model

_Provide here details of the Data Model, how it is extensible, how to preserve backward compatibility, etc._

There are a few pieces of data persisted by these custom authenticators, in various locations:

#### Data stored within Cloud Identity

For some of the planned integrations, it is required to persist some data within Cloud Identity.

### 6.3. Multi-tenancy

Not relevant to this design, as the product of this design does not run within Cloud Identity, but rather is a consumer of Cloud Identity.

### 6.4. Management Interfaces

_Provide details of all external interfaces, API and CLI etc._

### 6.5. User Interfaces

_Provide here an overview of technologies used for User Interface as well as User Interface details of this design._

### 6.6. Deprecated Interfaces

_Provide an overview of external interfaces that are being deprecated in this release and tie this to scenarios that are being deprecated._

### 6.7. Audit Logging

_Outline what audit events will be generated by this design and details of such events._

### 6.8. Reporting

_Provide here an overview of technologies used for Reporting as well as reports relevant to this design._

### 6.9. Security Considerations

_Aspects of the design that ensure that the product doesn't create vulnerabilities._

### 6.10. Integration

_Provide here an overview of technologies used by this design to integrate with other IBM and non-IBM products and tie back to the integration related scenarios outlined in the Blueprint._

### 6.11. PII/GDPR

_We need to be wary of how we use Personally Identifiable Information (PII).  Because of the EU GDPR requlations, IBM could face fines and have our software blocked/banned from EU if we don't comply with the GDPR rules and regulrations. For this design, note what PII data is used, why it is used/needed, where it is stored, and how long it is retained. If this design does not impact PII, then leave a sentence declaring as much._

### 6.12. Migration

_Include all migration considerations, including both across product versions as well as how customers can move data from one environment to another. Typically, customers have multiple environment such as Dev, Function Test, Integration Test, Performance, Production, Disaster Recovery._

### 6.13. Future Considerations

_List here items that need to be considered for future_

### 6.14. Discarded Technical Options

_List here technical options that were considered but discarded_

## 7. Operational Impacts

### 7.1. Deployment

_Describe what is required to deploy the new/changed capability.  Include considerations such as restart, downtime, and whether it can be rolled back._

### 7.2. Automation

_Describe what is required to automate the deployment_

### 7.3. Security

_Describe any impacts to security classification, data storage and privacy, certificate/key management, new identities, etc._

### 7.4. Monitoring

_New/changed monitoring interfaces, logs, trace data, etc.  How this data integrates with existing data.  Consider that you are writing this for the person who needs to update the status server used by Ops._

### 7.5. High Availability

_Provide an overview of architectural approach of how this design is made highly available._

### 7.6. Disaster Recovery

_Provide an overview of this design supports disaster recovery._

### 7.7. Performance and Stability

_Provide an overview of considerations to make this design performant and scalable.  How will you deliver key performance indicators and satisfy SLOs._

### 7.8. Other

_Indicate anything else that an Operations person needs to be aware of._

## 8. Documentation

_This section will contain information which is geared to the IDD developer so that they know how to document this new functionality. The section should provide enough information for the IDD developer to write the official documentation_

## 9. Testing

_This section will contain any unit test cases which will be implemented/executed as a part of this design.. The following table provides an example of what the test case design should look like_

# DOCUMENT PROPERTIES

**Authors**: Austin Bruch (afbruch@us.ibm.com)

**Status**: DRAFT

**Review PR**: _Link to the Github Pull Request that contains design review comments_
