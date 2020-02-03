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
	- [8.1. Version and Release Compatibility](#81-version-and-release-compatibility)
	- [8.2. Source Code Management](#82-source-code-management)
	- [8.3. General Testing Technology/Approach](#83-general-testing-technologyapproach)
	- [8.4. Test environment infrastructure, architecture, and topology](#84-test-environment-infrastructure-architecture-and-topology)
- [9. Documentation](#9-documentation)
- [10. Open Questions](#10-open-questions)

<!-- /MarkdownTOC -->
</details>

## 1. Overview

This document will outline and describe the nature and design of how IBM Cloud Identity can be leveraged to provide advanced authentication and authorization features for RedHat SSO (Keycloak) deployments.

Keycloak provides a first-class extension mechanism where custom code can be integrated into an authentication flow, allowing developers to gain control of an end user's authentication context, providing a way to integrate third party services to extend authentication capabilities. This document will describe how we will build such extensions, describing the APIs we will leverage from IBM Cloud Identity.

## 2. Traceability

[GitHub Repo](https://github.com/IBM-Security/cloud-identity-keycloak-integration)

[Aha! Epic](https://ibmsecurity.aha.io/features/CIC-986)

[Jenkins folder](https://sec-cloud-identity-jenkins.swg-devops.com/job/keycloak-integration/)

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

### 5.1. Personas

Standard Cloud Identity personas are in play here:

* **Alice** - Keycloak (and subsequently Cloud Identity) developer. She consumes and integrates the plug-in extensions described in this document to integrate features from Cloud Identity into Keycloak.

* **Jessica** - End user of a customer's Keycloak deployment. Jessica will benefit from the CI - Keycloak integration by being able to use additional authentication factors to access her Keycloak account.

### 5.2. Scenarios

#### 5.2.1. As a Keycloak developer (Alice), I want to be able to download Cloud Identity Extensions to drop into my Keycloak Deployment.

_coming soon_

#### 5.2.2. As a Keycloak end user (Jessica), I want to be given the option to setup passwordless authentication for my account so I don't have to use my username and password anymore. (IBM Verify - QR Code variant) (Inline Verify registration)

During a _normal_ authentication flow, Jessica will be prompted with an option to setup passwordless authentication by installing IBM Verify on her mobile device, and completing the registration process in her current session. See the below sequence diagram for what that flow will look like:

![IBM Verify Inline Registration Sequence Diagram](https://www.plantuml.com/plantuml/svg/dLVVRzis47xNNt7mGz5YSPnsRqQTDdFMmXkAficsbsKOQF5a4w58bQIS2SF-zzrHFopPKTBK3xOa7k_kk--xe_pU1-52cUia2IfeX5-nIhKLT_03BBKj9Qma6bggu0zqAgjWzTEdzl8QDye79uAo1ZveUvya8WtMmUyUNT9icrmv6p0DAE6YA2OWFF-sgssupdgvWcNfWytXeWnRVfeASbdleoUcR_bvTRcCAtpHNxXK3ZcJAgvsTtsJWNlV-Ty_8Z1biARCr-XWydPz_kVPsTbVauJ1JSy9mnmkqXIzXw93A8f2ytOgI48MqtCsdbEXP4m1Ihg5OFTLpgXQOX-MzzN-Muiko6DRshKstigee4H0CDRbGX-OGqPKmKk9GIZjmPO1R0O-jGM-Eeh-GHdbjz_XQ9zJf6qEbwXnmqcbrWHdDTi-u2-9vjDpfdGE7p60g0iGx1sQMtFr-UOtcErUdvxDqCZ2AXDc4ZDHwZ2B9f1gHJx_LdAHMVkYkVEOEWoBjN0lDaxGWr0Lk6WMQIcgN6YVSDmkvsiCfJE33Dhy6eiRaWeE340Py7GN0H5N3gT4UxjjNrc6Tj1YN7nONHvIJC9YuHoNj4dlMLv0PO3_KD_wn1OX0VeqgNLjGWBAzdgBcwH5Jo88i1KxWaiSEeU-i4Ogitam0Ig9BKqu0IekisuDtXfes7j3xB6LZ_6xm1qmLmCpU5yxKoQp9_ITwZ86B8twMYA1xEVVQndlRHeROAGqzofiXmXfDCW-jVivC8Vb5jCxhbiinPOcXOkJ3qas_NaeD46LrQtv-7ChKJ5mzSwZSEbsmNvgURv-ysCo37T0hYSjnROQ2JdO6odVg1s9C68QmKFi6jiuRAPthOWJs52mW8OQP1hxM3uuwscaPueE2wQ5I1s3EwlRop-5y7a17qSLAUf3ypKsLayx54bLVOl1J3b0q6mYgtTVNMebhinrrBt7HN0amNzFJq__UmhWbsiGHa8kJ4b3b0nT1UoC9mP8vGijgiZTBpmtKi6FQKhwCiVwCYyqKkRzh0_d4FVbWDBneBORKAsJzfWoagf7Os5ACsn0G24sM7VDk5knesD0h8acXk9JOGprwgnlcGWTEf0Kn_Em3BOJTspd-WYgKxeXBx5-7A9EeAV-tAvP5NJgmaiAITpxuyHUHQVQsWAkhDQCdiuCT3kXuye-PCTQ3pX1oRdBxDINwro5M3q1VcjTEFOp8iqObubi1uau4c4SAslauolEIncBI6vw_VpSmTBFwbi7JDiqFJLsVfYyGN5QOShEvC5n_4cukyEZdj3wCfwQWxs7RpBTcnDLR_2QDTZrn2jPPulQun017scFecZJcBflQGxcomlgsj4ksUTYwdZy6_Dgsw8xQYdKU7kCQhMMAIEF6KtMLI4ylT-IDsxuxoNeUSGyHGY3VazVzCVZVm00)

<!--
@startuml

title Keycloak + Cloud Identity Verify IBM Verify Registration Flow

actor User
entity "Protected App" as App
entity Keycloak
entity "CI Custom Authenticator" as Authn
entity "CI OIDC" as OIDC
entity "CI Directory" as Directory
entity "CI Authenticators" as Authenticators

autonumber "<b>[000]"
User->App: Access protected application
App->User: Redirect user to Keycloak for authentication
User->Keycloak: Access Keycloak for authentication
Keycloak->User: Initiate normal authentication flow (details out of scope)
User->Keycloak: Finish normal authentication flow (details out of scope)
Keycloak->Authn: Delegate control of authentication flow
Authn->OIDC: Get access token\nPOST /v1.0/endpoint/default/token client_id=foo&client_secret=bar&grant_type=client_credentials
OIDC->Authn: Return access token
Authn->Authn: Store access token in session for re-use
Authn->Keycloak: Get Cloud Identity User ID for authenticated user
Keycloak->Authn: Return Cloud Identity User ID for authenticated user if exists
opt
    Authn->Directory: If Keycloak user does not have a corresponding Cloud Identity User account, create one\n POST /v2.0/Users
    Directory->Authn: return new User info, including unique ID
    Authn->Keycloak: Associate Cloud Identity User ID with Keycloak user
end
Authn->Authenticators: Check if user has registered IBM Verify already\nGET /v1.0/authenticators?search=userid=foo123
Authenticators->Authn: Returns set of registered IBM Verify authenticators for given user
Authn->Authenticators: If no registration exists, get tenant-scoped Verify Registration Profile info\n GET /v1.0/authenticators/clients
Authenticators->Authn: Returns tenant-scoped Verify Registration Profile info
Authn->Authenticators: If no user registrations exist, initiate new registration\nPOST /v1.0/authenticators/initiation?qrcodeInResponse=true {...}
Authenticators->Authn: Returns QR and manual entry codes to display for Jessica to consume to complete registration
Authn->Authn: Save registration QR and manual entry codes on session
Authn->User: Render opt-in Registration page with QR and manual entry codes available for scanning/manual entry
alt Jessica opts in to register
    User->User: Scans QR code with IBM Verify mobile app (or uses manual entry code)
    loop Poll on interval
        User->Authn: Registration form auto-submits on a short interval
        Authn->Authenticators: Poll to check if a new registration has been created for the given user\nGET /v1.0/authenticators?search=userid=foo123
        Authenticators->Authn: Returns set of registrations for Jessica
        alt Registration completed
            Authn->Keycloak: Mark authentication as success
            Keycloak->User: Redirect to protected app
            User->App: Access protected app
        else Registration not completed
            Authn->User: Re-render opt-in Registration page with the same QR and manual entry codes
        end
    end
else Jessica does not opt in to register
    User->Authn: Registration form submitted with "bypass" option
    Authn->Keycloak: Mark authentication as success
    Keycloak->User: Redirect to protected app
    User->App: Access protected app
end
@enduml

-->

#### 5.2.3. As a Keycloak end user (Jessica), I want to be able to sign in to my account using IBM Verify and QR Code verification as passwordless authentication.

Once Jessica has completed IBM Verify enrollment, she can now opt in to authenticate by scanning a QR code with her IBM Verify mobile app. She should be given an option to sign in using traditional methods as well, as there is no way to know whether all users have setup IBM Verify. See the below sequence diagram for what that flow will look like:

![QR Code login flow sequence diagram](https://www.plantuml.com/plantuml/svg/ZLRRRXCn47tVhnZr0Lf1s8B4IqMhXcrQhUWbD2ql525dTpQnujYBxMs947mxOtklf1IgjCdQCsVcdBaqnyOoRQkrY2BBhK3uW9jCABQ25v08LUMGvYZfQWEVKFDY0n-luLmjk8JJS_KGHSzlbjm0lHWOBXU48BX4oDHwJNvHn3AhDDmQr56DipFLoc9cCOTnMUu0C-wzkMtYjzP92abbh5h3kB9BTvenWWn-T2BxbbVfIU9lt8V-nS3NjCxTqP_6nC-d7cphpqIdiafMwpbgs7atF_gylx__PIToz4P7nE80nbc6na3PScHbALmChcH45gCZPtm0rvXpJHPGqIDOrGbVa5wioytv1Vp6eWtob4jxDpeAV0xW32LgPgb2ldubMo0ySBjq9KrKZi1a3WKJOiwo5QZIuPWeY44eJb87Oe65y5QjKDx9wTNi1kBxrw_sOvHvgRYqSOu5guIDQvDCS4hjAyyF2wMUrKy6CutsSCxqiuLcT60t9HxMbtJb-uu94xd0FHhNQ2ij1qbqITOsCweM3ao061jw8avUBeqZKdxes7L3u9c-lsYw_XeNt5ZjjGLgu89JftDPg3jvDWdqLSm6676W4XJiZhUPzCBqaVMwRfecnRe93o2Ld9IXGZQps2JnXcfG1Aluc-xdWr7jlHsUGBAc-aAfLLNwy5ubsWwsecvMWgPwqQNIjUBxYelS--e0zqXpEVX1GpOZ87Fg_Sx1ztbtFyjObvt7wYb5PIYNIc94APVkoIoLjfIvHNtF1Ep-Ucls8g2V1hHctUulfx2R6ZKorNpDgL3EUbljAP7ll88Y_o5u_8FdFptaKyeFGNUdaykJzF8ilaal9bUtDt4olamcvvEJ-7ISkhVPRP9CPhD0Z0bgqQ93qMXevjmkhisyrO1KLxVpKEb-nddOHVDDa2obPQ1fxPhAZr5HTIgqm8zicMOy6-0MbzevMr8NDUXFPJSsHcM-nHz9qBT2FSj3aFwEl61wzSTIT9kyPhF5O6ilqqeUB77OnU_SkWafzrhlVon-RuV2u5_g99NzdrgDXooS5_r_BGLQVEaMcGw3r0QOkbbwh4I3kTisf_F8_HxJ7_fAy1i0)
<!--
@startuml

title Keycloak + Cloud Identity Verify QR Login FLow

actor User
entity "Protected App" as App
entity Keycloak
entity "CI Custom Authenticator" as Authn
entity "CI OIDC" as OIDC
entity "CI Authenticators" as Authenticators
entity "CI Authn Factors" as Factors

autonumber "<b>[000]"
User->App: Access protected application
App->User: Redirect user to Keycloak for authentication
User->Keycloak: Access Keycloak for authentication
Keycloak -> Authn: Generate login page with QR Code and fallback options
Authn -> OIDC: Get access token\nPOST /v1.0/endpoint/default/token\nclient_id=foo&client_secret=bar&grant_type=client_credentials
OIDC -> Authn: Return access token
Authn -> Authn: Store access token in session for re-use
Authn -> Authenticators: Get IBM Verify Registration Profile info\nGET /v1.o/authenticators/clients
Authenticators -> Authn: Return IBM Verify Profile information
Authn -> Factors: Initiate QR Login\nGET /v2.0/factors/qr/authenticate
Factors -> Authn: Return QR code and lookup info for status polling
Authn -> Keycloak: Build and return login page with QR code
Keycloak -> User: Render login page
User -> User: Scan QR code with IBM Verify on phone
loop on short interval (~5s)
    User -> Authn: QR Login form auto-submits
    Authn -> Factors: Poll QR Login status\nGET /v2.0/factors/qr/authenticate/{id}
    Factors -> Authn: Return QR Login status\n(PENDING/TIMEOUT/CANCELED/FAILED/SUCCESS)
    alt If QR Login result is SUCCESS
        Authn -> Keycloak: Lookup authenticated user by User Id from successful QR Login
        Keycloak -> Authn: Return authenticted user matching User Id
        Authn -> Keycloak: Associate authenticated user with session
        Authn->Keycloak: Mark authentication as success
        Keycloak->User: Redirect to protected app (exit loop)
        User->App: Access protected app
    else If QR Login result is not SUCCESS
        Authn -> Keycloak: Authentication not complete, re-render QR Login Page
        Keycloak -> User: Render QR Login Page
    end
end
@enduml
-->

#### 5.2.4. As a Keycloak end user (Jessica), I want to be given the option to setup passwordless authentication for my account so I don't have to use my username and password anymore. (FIDO/WebAuthn Device) (Inline FIDO registration)

![Inline FIDO Registration](https://www.plantuml.com/plantuml/svg/pLPBR-Cs4BxhLmnyMBHeR6NJcz6a6zZDGYsA14btzz0K1IsER28KgPAKKsEn_xqprCEoOk_sSMaEYcJEwvjl7dpZWt2XBdMI11Kqma-uoxKLJ_0DBBIj9MGI3Htju3qwLUmWCrePX9jiUPj-mDLr7JO6xd6jV70YA6lWHjld915vi0xUUNH9Qs1ovsp0FA24wwgQWF3ylplj7FVIYmmMjG-s1FR1l-Q2J3Pwx7KeUPij5_64NuO7I-MG8zd5q_vhA78JG_LHe7sdyEjWJLskqC7akzNLh-VdvxzD4eOplQAevt2Tv-WzL3qcKLMQGwGK92GnlMBXEQL6HgTGqoS4kyzmGVaHUsYirzZl97eddrFfpZf_cL51YO1Wh2k57eb3GUJ0LnA3KDg3hGFO0dnkA_pwXVSRPPJV_0j3-vWYKNDOeiOr1vLR4vpLB7l4NXB5frTCuXpUOW3H923O9pIFvkxsuHT8jwzdvoaQMLbbGYgn4BKEQHI1N2ko-RkIbuMrhzelZxd3SBaIxjNQ2VeXx2gyR0_fA5Quq3vXlttCznXgPmuYwE9h9HweLF1009G1JryH477bS4gqTshxp3AqKNjnyY5RZYccmkB2UPdIDhn_P0LK0VWdTQfFR1KIeBysjBung82AVRr59MdHKn44s8WjmIKEdKDVMIELMHyDW59YQnFEW9BBh5k3ZmPQzYw8FPRoqN_lk0Vc6c06dnjZoXJsZ9wvhgF3sgW_QYIGm_W7BUEzpMC3d4ZDimgRGuGq36J7KJi0vh3OOFx42OivsD1iS778eUDUZ_CF96vLJYdH15Fk7itR7pg0HMCcBPIq5waRZ4V_lKVXyiqbsstgzFN5jqdhTqIlfsgAZJNmFV3i8y_4CgpLbgelWXa38JwDXM48RGsSmPgC1pJK4jFOkQtnUzGxplGThGP5JfW1uY-YemOkrFeuoAXLDKed0VrDVqTRhP6-YmAVRxKH-iZX0GjD3hYkkd59zHRj3yNwaVCbkEb7LsNoKof2G5gfh9jIYqLF7sUpsQUJ-TWAfspj6zyyl46A87XHKD0CybqMyVt8MFDuG1pukiJcjQmqKlZ3gBiaT0l8I49FqKpLQ5DNOerDFo1jEYdtX0o4E9n4wD17G9QO8tRVbMLinMPz76uX_pAdPmH7qx3ti7BqfD1eXvPiSmXjWyf5tOksrcS37tqA1s5IZahmzQfKeNKwuA81CPWNVNqyH8sN2ZJXQAly9_uR4otzxFju2G3BOzo8U-NZi-rduPx6UvC6aw_Z2eewu-j0V_sWv1xSLORfF7MVIL1J3NQyztk02k0q_r_WXYt7YfkiTfNmTEjgskJ_0fWsmHjwq8tuBm00)

<!--
@startuml

title Keycloak + Cloud Identity Verify Inline FIDO/WebAuthn Registration Flow

actor User
entity "Protected App" as App
entity Keycloak
entity "CI Custom Authenticator" as Authn
entity "CI OIDC" as OIDC
entity "CI Directory" as Directory
entity "CI Factors" as Factors

autonumber "<b>[000]"
User->App: Access protected application
App->User: Redirect user to Keycloak for authentication
User->Keycloak: Access Keycloak for authentication
Keycloak->User: Initiate normal authentication flow (details out of scope)
User->Keycloak: Finish normal authentication flow (details out of scope)
Keycloak->Authn: Delegate control of authentication flow
Authn->OIDC: Get access token\nPOST /v1.0/endpoint/default/token client_id=foo&client_secret=bar&grant_type=client_credentials
OIDC->Authn: Return access token
Authn->Authn: Store access token in session for re-use
Authn->Keycloak: Get Cloud Identity User ID for authenticated user
Keycloak->Authn: Return Cloud Identity User ID for authenticated user if exists
opt
    Authn->Directory: If Keycloak user does not have a corresponding Cloud Identity User account, create one\n POST /v2.0/Users
    Directory->Authn: return new User info, including unique ID
    Authn->Keycloak: Associate Cloud Identity User ID with Keycloak user
end
Authn->Factors: Check if user has registered a FIDO device already\nGET /v2.0/factors/fido2/registrations?search=userid=foo123
Factors->Authn: Returns set of registered FIDO devices for the given user
Authn->Factors: If no registration exists, get tenant-scoped FIDO Relying Parties info\n GET /config/v2.0/factors/fido2/relyingparties
Factors->Authn: Returns tenant-scoped FIDO Relying Parties info
Authn->Authn: Store FIDO RP info in session for re-use
Authn->Factors: If no user registrations exist, initiate new FIDO registration\nPOST /v2.0/factors/fido2/relyingparties/{rpId}/attestation/options {...}
Factors->Authn: Returns various FIDO Init data to use in UI for Jessica to consume to complete registration
Authn->User: Render opt-in Registration page with embedded FIDO Registration data
alt Jessica opts in to register
    User->User: Initiates FIDO registration, follows browser prompts to consume their FIDO2 device.
    User->Authn: Registration form submits FIDO attestation data
    Authn->Factors: Submit FIDO attestation result\nPOST /v2.0/factors/fido2/relyingparties/{rpId}/attestation/result {...}
    Factors->Authn: Return state of attestation
    Authn->Keycloak: Mark authentication as success
    Keycloak->User: Redirect to protected app
    User->App: Access protected app
else Jessica does not opt in to register
    User->Authn: Registration form submitted with "bypass" option
    Authn->Keycloak: Mark authentication as success
    Keycloak->User: Redirect to protected app
    User->App: Access protected app
end
@enduml
-->

#### 5.2.5. As a Keycloak end user (Jessica), I want to be able to sign in to my account using my FIDO/WebAuthn device as passwordless authentication.

![FIDO Authentication](https://www.plantuml.com/plantuml/svg/bLPBRnen4Bxlhx0uf8S6bkOODIYKDD6sgH8bxQcfAcFFWeMnBTjBXAByzuvtlIyWZtA0zSxxc_d6d3jFhI_MCacyy1B8TzWogUcAV2GpgGjECWuAHLjocLtSa6dXb-7Cg1TQaKkf7vE4Cgyj-UN09b5tS6kr1-Q1awan0q9T-AsbTOn6UvQHMU6yNdVzQrlPuHlLrRp9BcQb93nq1PTb7gwKnMVChV1Q5UivM3BuF9_y7e_7VmP9o7KumPHEoPGnS8wO9c5gZ8pr9QWnd0JbKt87N5ZK80KUYTSjKZaMJtkuHF-rHXFaDPD6DfnKDP-IAr1WgGSYzK8eOkW2oAFmoweLhZ16Mq-euYIdKiufMn5jWZENb1w2gm1Iy8HwLGvUhq0zgDkR-vyatNmQZLDGt6YXVCeXfuNqQLHXKc1-VmK_oxK-YYS7p88_crDxjB0KN_YjWRCeH54vBLIw90Jkr787lh2gbqIRPDIvnvP1JuLWsGvFOTO2PXQ627zh6DjS5LYYSWToAzI2tE9ORz4ShTObm0_gwYjMpBJAnIBTd63XUMMTveBhazHMbWOD1RWakjuluVKm-qNH3RnXy-ugCoKGN1_zNOUfU8YDVAkUzCcQZ3-drE5eXg1f71NoD1gDdjyeDmJUvRwXMvnNVamMOeETGaPy2pKm-i6HkTMFaIPChuq4p7a3LkGDiUhQMevyAOJavJJRAl9hKz-ZIqrGnJ5aQrMoi9NFbbex22bLAgrF3Xl1u1YnbxZFckHn9QoD3vFOvIhW0WXTx5WsWUgM5_ErYFFOu7sWdGVrGhYTpHjceejUmzx_Rxe5Xmn_Ly_RZF2Ey8Mh06FQeW-Z5GyWX5sOyOIYoooFPlqf4GvRLZ8wBsH2y7EWzTTQhmhJGvbNUtQ-BRk7M90Ssz7njGjlS7rWXzPxftRTU4RgiMMe8Vf_ARUfSvgLf3kGNWb9f6xheBlvVr2xsiM4DfZqijwxQN0IUjTIgVt6rPM0n25_kHbA-tSqv8MiSoeay3seU_m3Qt5SI_xXY-GSl_3lnJy0)

<!--
@startuml

title Keycloak + Cloud Identity FIDO Authentication Flow

actor User
entity "Protected App" as App
entity Keycloak
entity "CI Custom Authenticator" as Authn
entity "CI OIDC" as OIDC
entity "CI Factors" as Factors

autonumber "<b>[000]"
User->App: Access protected application
App->User: Redirect user to Keycloak for authentication
User->Keycloak: Access Keycloak for authentication
Keycloak -> Authn: Generate login page with FIDO support and fallback options
Authn -> OIDC: Get access token\nPOST /v1.0/endpoint/default/token\nclient_id=foo&client_secret=bar&grant_type=client_credentials
OIDC -> Authn: Return access token
Authn -> Authn: Store access token in session for re-use
Authn -> Factors: Get FIDO Relying Party information\nGET /config/v2.0/factors/fido2/relyingparties
Factors -> Authn: Return FIDO Relying Party information
Authn -> Authn: Save FIDO Relying Party information in session for re-use
Authn -> Factors: Initiate FIDO Login\POST /v2.0/factors/fido2/relyingparties/{rpId}/assertion/options {...}
Factors -> Authn: Return FIDO Init Authentication payload, given to Jessica's browser to complete verification
Authn -> Keycloak: Build and return login page with FIDO support
Keycloak -> User: Render login page
User -> User: Choose to login with FIDO device, follow browser prompts to authenticate using FIDO device
User -> Authn: Submit FIDO assertion
Authn -> Factors: Submit FIDO assertion to Cloud Identity for verification\nPOST /v2.0/factors/fido2/relyingparties/{rpId}/assertion/result {...}
Factors -> Authn: Return FIDO assertion status with corresponding userId
alt If FIDO authentication is successful
    Authn -> Keycloak: Lookup authenticated user by User Id from successful FIDO assertion
    Keycloak -> Authn: Return authenticted user matching User Id
    Authn -> Keycloak: Associate authenticated user with session
    Authn->Keycloak: Mark authentication as success
    Keycloak->User: Redirect to protected app
    User->App: Access protected app
else If FIDO authentication is not successful
    Authn -> Keycloak: Mark authentication as failed
    Keycloak -> User: Render error page
end
@enduml
-->

#### 5.2.6. As a Keycloak end user (Jessica), I want to be able to complete a MFA challenge by using my registered IBM Verify mobile app via Push Notification.

![Push Notification MFA](https://www.plantuml.com/plantuml/svg/fLRRRkCs47tNLmpyi4XGEquV-X8qwRgs4gXTEuOl2nJT8g2baKs49WMISZO8qc_lK99riQruWGP9R8bpPiwS6GxvsLYcRReLdcUv5GX_ucie57k6ds0eL1f14A6afLVuYfh7hp2-7y2ECvYcPWCJPNdCGsQvad0lr8ldiT0g3Kk3sYk0dQbM5aEB4GoIf0FCkC_zwZvWQJqCO9WQgxOmIEt6lIN_IkSuUYFhbe_1Q9Yjk2_rXGRMbE3gLOknIsICTgEY2YIB5uSGIZdBDhSilfC2gLKotQvGG-VNrTtVrzVN_tGyfqZlZXA_WK4OeZ6GbBAm9155S8yiUdVE-0Pc675D5f3I8rXL5IScYLc3TE5_Rr46-GXIhlNk88yOI6uvimWnryP2d1Up2VicBrHgGSLWGfLWvjXkkA57b4npTPbnSKuhDaErJGJ-RyTrnba5Rc240jUESjEPaolCsuZLw_vDFg14xG2kavS196oDNbxE1kN7bMLS7kw2BBLWT2Wd5T4LwHJ16TfKj_cf0hi6le47jC3oobdrZAJ9z76-WFxkvwlhFieeKLpQVeGnIuNj5oQXuEJrYKUtiLAVYYU3eKPxksBwqrepUc5V4xmj5caf8yA4yLpWOxfr4XN9mcPEGcB314WfGqzEUIUDnXxfqGHMUo_FK-EQ6ulQRO36Sb5vp2IbWlbx0PfclnbaEjpSgXSgw6tdpLa7qNl7QmOxaPiGFnYy9VCsCAaGgzDImCM1PNHvuBmOCaKF44N3rv8HOJGqNemAUTPcBznkMiMHszn9ly9-gD8-w5cDQbpQzQaNk9man-U4Sv1DD9DWI4SAbsjWEyO5Mz41L4Lgz7wpIFstnZFbqz-vuwa8OU3jwkhg_MmZr37xJKq7earfE2iXY5XB0nm23qhUvgCQBWK1DuBS_Cchca_xZB_pyeVp6B8JOtf6au3qswFBUPkl3i9dgLv4XD7wbFpq6-6EXwubpQiCBpsXLE9UcutIbdAXak-OW8j_Vp6N7j3Ftd-XWOjB1BF9gdecNMsvTNCWIxX8FiErTiVK6HvJow7jE_vSsVjlF7el8tzK-VRO5rD_CWec3zr5CFOVbulkS30P-b_yKNSvzsTF8tyIxB_VpmRBqVABFrbqVmyUn_vYzjVJ_I1mjb_zMN1_-3HV3eV-V9vhoeI583v1X3gVPZlGkLVOP-QbdFKpVipqy-6vHpSHasQJkSJj0KVt2bUo-YK4Bl0xj-3wuR94dxcuP7OepAbMgrAHoXwbymCA5BKe8OscgjrrcXAuO65nuUYw-QiFzalTtOUxQrgtFfjaJ3CCetDaMufs0jnA3RMc-U1IkQhOoHpiFjtVP_f7z_h_0000)

<!--
@startuml

title Keycloak + Cloud Identity Verify MFA via Push Notification Flow

actor User
entity "Protected App" as App
entity Keycloak
entity "CI Custom Authenticator" as Authn
entity "CI OIDC" as OIDC
entity "CI Authenticators" as Authenticators
entity "CI Authentication Methods" as AuthnMethods
entity "CI Authn Factors" as Factors

autonumber "<b>[000]"
User->App: Access protected application
App->User: Redirect user to Keycloak for authentication
User->Keycloak: Access Keycloak for authentication
Keycloak -> User: Initiate first factor authentication\n(out of scope for this scenario)
User -> Keycloak: Complete first factor authentication\n(out of scope for this scenario)
Keycloak -> Authn: Delegate authentication to custom authenticator\nGenerate MFA UI page
Authn -> Keycloak: Obtain Cloud Identity User ID for authenticated user
Keycloak -> Authn: Return Cloud Identity User ID
Authn -> OIDC: Get access token\nPOST /v1.0/endpoint/default/token\nclient_id=foo&client_secret=bar&grant_type=client_credentials
OIDC -> Authn: Return access token
Authn -> Authn: Store access token in session for re-use
Authn -> Authenticators: Get registered authenticators for the user\nGET /v1.0/authenticators?search=owner="{userId}"
Authenticators -> Authn: Return all registered authenticators for the user
Authn -> Authn: Store registered authenticator info in session for re-use (authenticator id)
Authn -> AuthnMethods: Get all signatures associated with the user\nGET /v1.0/authnmethods/signatures?search=owner="{userId}"
AuthnMethods -> Authn: Return all registered signatures for the user
Authn -> Authenticators: Initiate Push Notification for user specifying available signatures\nPOST /v1.0/authenticators/{authenticatorId}/verifications {...}
Authenticators -> Authn: Return verification ID for status polling
Authn -> Authn: Store verification ID in session for status polling
Authn -> Keycloak: Return MFA page with directions for Jessica
Keycloak -> User: Render MFA page
User -> User: Acknowldedge Push Notification on device (async)
loop on short interval (~5s)
    User -> Authn: MFA form auto-submits to poll status
    Authn -> Authenticators: Poll Push Notification status\nGET /v1.0/authenticators/{authenticatorId}/verifications/{id}
    Authenticators -> Authn: Return Push Notification status\n(PENDING,TIMEOUT,CANCELED,USER_DENIED,USER_FRAUDULENT,BIOMETRY_FAILED,VERIFY_FAILED,VERIFY_SUCCESS)
    alt If Push Notification result is SUCCESS
        Authn->Keycloak: Mark authentication as success
        Keycloak->User: Redirect to protected app (exit loop)
        User->App: Access protected app
    else Push Notification result is not SUCCESS
        alt If Push Notification result is PENDING
            Authn -> Keycloak: Authentication not complete, re-render MFA page
            Keycloak -> User: Render MFA Page
        else Push Notification result is failed
            Authn -> Keycloak: Mark authentication as failed
            Keycloak -> User: Render error page.
        end
    end
end
@enduml
-->

#### 5.2.7. As a new Keycloak end user, I want to be given an option to enroll IBM Verify so I can use it for passwordless first factor authentication, and second factor authentication via push notification. (New user Verify registration)

_coming soon_

#### 5.2.8. As a new Keycloak end user, I want to be given an option to setup my FIDO/WebAuthn device so I can use it for passwordless authentication. (New user FIDO registration)

_coming soon_

#### 5.2.9. As a Keycloak end user (Jessica), I want to be given the option to specify a phone number to receive OTPs via SMS as a second factor of authentication. (Inline SMS registration)

_coming soon_

#### 5.2.10. As a Keycloak end user (Jessica), I want to be given the option to specify an email address to receive OTPs via email as a second factor of authentication. (Inline email registration)

_coming soon_

#### 5.2.11. As a new Keycloak end user, I want to be given an option to specify a phone number to receive OTPs via SMS as a second factor of authentication. (New user SMS registration)

_coming soon_

#### 5.2.12. As a new Keycloak end user, I want to be given an option to specify an email address to receive OTPs via email as a second factor of authentication. (New user email registration)

_coming soon_

#### 5.2.13. As a Keycloak end user (Jessica), I want to be able to complete a MFA challenge by using my registered phone number to receive an OTP via SMS. (SMS OTP)

_coming soon_

#### 5.2.14. As a Keycloak end user (Jessica), I want to be able to complete a MFA challenge by using my registered email address to receive an OTP via email. (Email OTP)

_coming soon_

#### 5.2.15 As a Keycloak end user (Jessica), I want to be able to modify my device registration and MFA enrollment in the case of getting another device, phone number, and/or email (IBM Verify)

_coming soon_

#### 5.2.16 As a Keycloak end user (Jessica), I want to be able to delete my devices/disassociate email and phone number after enrolling to the various authenticators (IBM Verify). 

_coming soon_

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

We need to handle, or at least address, the need to properly propagate data cleanup upon user deletion from Keycloak. In other words, if a Keycloak user that has registered anything with Cloud Identity is removed from Keycloak, that user's information should be removed from Cloud Identity as well. It is unclear whether custom authenticator extensions can provide such support, or if this will be the responsibility of Alice. If the latter, this needs to be made apparent to Alice.

### 6.12. Migration

As this is a new project, we do not yet have any migration concerns. As we iteratively release new versions/artifacts, we will address this concern as needed.

### 6.13. Future Considerations

1. Based on current limitations in the Cloud Identity APIs, these custom authenticators are required to provision a shadow user account in Cloud Identity to associate authentication factor registrations with a user resource. Ideally, we would be able to supply a user identifier that does not correspond to a user in Cloud Identity's user registry, so we could supply the unique user identifier from Keycloak's user registry instead of having to create a user in Cloud Identity. This eliminates some of the complication of managing linked user accounts from one directory to another.

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

One aspect that needs validation for this scenario: in multi-node Keycloak deployments where a custom authenticator could be executed on different servers, is there any kind of statefulness or node affinity established for a given authentication session? As a majority of authentication flows require multiple steps i.e. an initial challenge/prompt, followed by a subsequent validation based on initial data that is stored on the user session, it will be essential that a user is returned to the same Keycloak node that their authentication session started on. This may already be the case, or perhaps in multi-node Keycloak deployments, there may be some sort of session sharing mechanism so that node affinity is not required. This needs further investigation. I've included this in the HA section as it pertains to multi-node deployments, but there may be a more relevant section.

### 7.6. Disaster Recovery

DR of Keycloak deployments is out of scope for us, and up to Alice. DR of Cloud Identity resources is also out of scope of this effort, and up to Cloud Identity itself.

### 7.7. Performance and Stability

Custom authenticator performance is dependent upon the following factors:
* Performance of the various Cloud Identity APIs leveraged by a given custom authenticator
* Optimization of Cloud Identity API requests made by custom authenticators (caching access tokens instead of requesting fresh ones per flow, etc.)
* Optimization of interaction with Keycloak user directory when user lookups are required, especially based on custom attributes (potential need for custom DB indices based on user lookup approaches)
* IBM Verify-related APIs (registration, QR login, etc.) all currently require polling from the client to be updated on a state transition (i.e. registration complete, authentication complete, etc.). This could impact performance in a large scale environment. We need to assess whether these APIs can work in a more performant way (long polling, push instead of pull API, etc.), and if this will impact Keycloak user experience in any way.

### 7.8. Other

None at the moment.

## 8. CI/CD Pipeline and Testing

This project will follow the existing patterns for Cloud Identity development, testing, continuous integration, and continuous release cycles.

Unit tests will be written for individual functions/features within the development code.

Integration tests will be performed by setting up testing environments with various Keycloak deployments (for whichever version(s) we intend to support), in both _new_ deployments and _existing_ deployments. There will also be a set of Cloud Identity tenants (from the PRODUCTION environment) made available to continuously test integrations between Keycloak deployments and Cloud Identity subscriptions. Given these environments, tests will be written to recreate both Alice and Jessica flows surrounding the custom authenticators being built.

There will be daily test runs executed against these various test environments, verifying continued support of known features/functions. Additionally, as active development continues, new tests will be written to verify newly developed features. These new features and tests will be executed by the _development_ build/test pipelines.

All of this will be orchestrated via the existing Jenkins instance setup within the Cloud Identity development organization. This project has it's own [folder of Jenkins artifacts](https://sec-cloud-identity-jenkins.swg-devops.com/job/keycloak-integration/).

Build/test/release pipelines will be setup to support automating development and release builds, which involves testing the produced artifacts. Once a stable, release-ready artifact is produced, the release portion of the pipeline will be executed. As described earlier, the artifacts produced by this project will be bundled JAR files that can be dropped into Alice's Keycloak deployment. The release portion of the pipeline will then be responsible for publishing the artifact(s) to a publicly-accessible artifact repository. Specifics are still TBD, but something like [Maven Central](https://search.maven.org/) is the leading candidate.

### 8.1. Version and Release Compatibility

Keycloak does not support older major versions. As a new major version is released, all users are encouraged to upgrade immediately. Only high severity security issues are fixed in old major versions, and only for a certain period of time.

RedHat SSO provides customers with an extended support window for a locked in version of RedHat SSO, which is a modified version of a specific Keycloak release. As new versions of RedHat SSO are released, there is a window of time where both RedHat SSO releases are supported, while customers are strongly encouraged to upgrade.

Given the above, we should target the following _levels_ of Keycloak and RedHat SSO:

* Latest Keycloak major release (including the most recent minor release of this level)
* Latest RedHat SSO release
* N-1 RedHat SSO release for a designated window of time, as requested by customers

A few notes:
* Keycloak does not follow semver, but it is a fair assumption that within each major release, minor/patch releases are typically used for bug and security vulnerability fixes, and _not_ any kind of destructive changes. We _should_ be safe from SPI breaking changes in these sorts of releases, though we should plan to explicitily upgrade our test environments to target the latest (minor) release within the current major release.

* RedHat SSO releases are essentially forks of a specific Keycloak release, with some changes made on top of Keycloak. Therefore, to be thorough and confident in our extension, our test environments needs to test directly against RedHat SSO deployments, not just Keycloak deployments.

We should also plan to setup a test environment that tests against the latest source level of Keycloak, so we can get a preview of the upcoming release at any given time. There is supposed to be a way to configure the docker image, when using a Keycloak docker image, to compile Keycloak from source instead of using the pre-built level of code that the image is spec'd to. This will allow us to gain visibility into upcoming releases _before_ they're made available, so we can prepare any necessary changes to the extensions we produce.

As of this writing (24Jan2020), the current releases are:

- Keycloak (Community): 8.0.1
- RedHat SSO: 7.3 (forked from Keycloak 4.8.*)

The tentative plan for Keycloak (community) 9.* release is end of February. By the time we are producing consumable artifacts, 9.* will likely be the latest released Keycloak version, and will replace 8.0.1 as our primary Keycloak target.

No release schedule information is currently available for the next major release of RedHat SSO.

### 8.2. Source Code Management

Given the requirement to support a small amount of Keycloak/RedHat SSO versions concurrently, we need to carefully plan how we intend to manage our source code so as to facilitate making changes for different Keycloak releases.

In general, our development work targeted towards the latest version of Keycloak should map to our repository's `master` branch. As we stabilize `master` in preparation for a release, releases will be tagged in GitHub, and published to maven or wherever we end up publishing our artifacts.

As new versions come out, any subsequent changes needed _for only older versions_ of Keycloak should be handled by forking the master branch at the last known common point, and these changes would be made in a branch divergent from `master`. The branch name should indicate the corresponding Keycloak version.

Branching diagram coming _soon_.

### 8.3. General Testing Technology/Approach

Given that a large amount of the scenarios/stories we are building involve browser-based web flows, we plan to use Selenium WebDriver for automated testing for a number of use cases. However, there will be some challenges there as many of the flows we are building require interaction from an external device, such as an IBM Verify mobile application or a FIDO/WebAuthn hardware key. Investigation needs to be done to figure out how we can integrate such technologies into a WebDriver-driven test suite.

Additionally, we will want to investigate if we can do API-based testing for some of the Keycloak operations we are integrating with. The feasibility of this is still TBD.

### 8.4. Test environment infrastructure, architecture, and topology

There are several combinations of Keycloak, RedHat-SSO, and Cloud Identity that we need to test. There is also a need to test with new Keycloak/RH-SSO deployments versus existing deployments. The sets of inputs are as defined as follows.

> Note: this document ultimately describes a generic test plan where explicit versions of Keycloak and RH-SSO are "Latest" (n) and "Second latest" (n-1). However, I will use concrete versions for more clear examples. Based on [the above versioning discussion](#81-version-and-release-compatibility), we will start with only targeting the latest versions. In the examples below, the mappings are as follows:

|Concrete|Generic|
|-|-|
|Keycloak 9|Keycloak n-1|
|Keycloak source|Keycloak n|
|RH-SSO 7.3|RH-SSO n|

<!-- |Keycloak 8|Keycloak n-1| -->
<!-- |RH-SSO 74|RH-SSO n| -->

**Keycloak:**

- Keycloak 9
- Keycloak build from source (to handle upcoming release) denoted as `src`

**RH-SSO:**

- RH-SSO 7.3

**Cloud Identity:**

- PROD
- PREP
- Rel-ITE
- Dev-ITE

> Note: Primary testing should utilize Cloud Idenity's PROD environment. Down the road, we should also integrate Cloud Identity's non-production environments, so we can be aware of any breaking changes coming up from the Cloud Identity side of this integration, before they get to production. However, given that we will only consume public, documented APIs from Cloud Identity, this risk should be pretty low, so integrating with these environments should be a lower priority than other aspects of the test infrastruture buildout. If we build this right, it should be pretty painless to substitute a Cloud Identity tenant from a non-production environment.

**Combinations to be tested:**

|Keycloak/RH-SSO Version|Dynamic or Persistent Keycloak Deployment|Cloud Identity Env|Cloud Identity Tenant|Targeted Phase|
|-|-|-|-|-|
|kc-9|dynamic|prod|keycloak-test-kc-n1-dyn-fra|1|
|kc-src|dynamic|prod|keycloak-test-kc-n-dyn-fra|1|
|rh-73|dynamic|prod|keycloak-test-rh-n-dyn-fra|1|
|kc-9|persistent|prod|keycloak-test-kc-n1-per-fra|2|
|kc-src|persistent|prod|keycloak-test-kc-n-per-fra|2|
|rh-73|persistent|prod|keycloak-test-rh-n-per-fra|2|

The tenant name structure is as follows:

```keycloak-test-{kc|rh}-{n|n1}-{dyn|per}-{fra|ams|wdc|dal}```

where

- `kc|rh`: Keycloak or RedHat SSO, respectively
- `n|n1`: `n` or `n-1` version, i.e. latest or second latest version
- `dyn|per`: dynamic or persistent Keycloak/RedHat SSO deployment
- `fra|ams|wdc|dal`: Cloud Identity environment where the tenant lives. This is not too critical, but adding it for visibiility if it ever becomes useful.

![Test Infrastructure Architecture/Topology](https://www.plantuml.com/plantuml/svg/ZPHDRnen48Rl-oj6N58ELfJcKBL2A558Fz938aXFAGSN3-m8hxsoPochgl-zOuVb8m9KBgkPUVq-CsEBcyWwS5FPec1YY_0TsvdrUWdtQqONoRi8pnWPRiEi9COPDm6BuY8koTKww0eiEUIsHgYzRLDG7AdvmAKlYfS7T5ACKr1g2AProVhUhJx1kwl-f2O7JPtIkgBPhXNj38o_gidayQT3jwBWNOME8mneM6E0NmrPCxYa8VmcBdF8y70xZczuejtntlVXHsqq8-2Q8fDR_0Sm-mlLoZdPIIPbSAQzmIq26G5Tzs4KQ8MW6_QL73IG3elSUOFGwwYN7N6qcN-4jVLj0iloKVT0nxsLmPy2vFSIIlNXMab--XelP-hZQ_Zt9E49GvIPa_Cno6vFXn39xqDoA13nRWpqxlBdcn4huXOcQD5HKy4NMKEsJVle33zJmBbVGsemRaplok0hx58dZP_6ZwDUvxFSZARIIfMqwTBiQXxqz9X0-Zulo0uos7c7hK2Qp5CDbCKv7ppfhX1eKVB1uvDtihqa4KZjR9TxDtnKVJZqxcfEMs_RtxlNqzPRTN63piXVnJy0)

## 9. Documentation

Extension documentation will be housed at the [GitHub repository](https://github.com/IBM-Security/cloud-identity-keycloak-integration) using various `markdown` files.

Documentation should include but not be limited to:
* Enumeration and explanation of available custom authenticators
* API Client entitlement requirements for each available custom authenticator
* Configuration options for each available custom authenticator
* Integration/setup steps, guiding Alice through using the Keycloak admin console to integrate one of more custom authenticators
* Walkthrough of sample integration(s)

## 10. Open Questions

1. What is the backing Keycloak directory technology? LDAP? Database? This may have implications on how we lookup Keycloak users when matching them up to Cloud Identity resources.

2. When performing the IBM Verify registration flow, is QR Code scanning the only way a user can complete the registration? It is not unforeseeable that a user does not have the camera functionality working on their device, which could hinder their ability to complete the IBM Verify registration process.

> According to the [IBM Verify documentation](https://www.ibm.com/support/pages/ibm-verify-user-guide), there is also support for a manual entry option as an alternative to scanning a QR code. According to the corresponding [Cloud Identity API](https://afbruch-demo.ice.ibmcloud.com/developer/explorer/#!/Authenticators/initiateAuthenticator), this manual entry code is included the response payload to the registration initiation request. So it looks like a manual entry option is also supported.

> However, when trying this out on my iPhone, I am only presented with the "Scan a QR Code" option. I do not get the option to do manual entry. This will have to be verified.

> Whether or not manual entry is included in the initial registration flow is TBD, but we know that it is supposed to be supported by IBM Verify and the corresponding Cloud Identity APIs.

## DOCUMENT PROPERTIES

**Authors**: Austin Bruch (afbruch@us.ibm.com)

**Status**: DRAFT

**Review PR**: [GitHub PR](https://github.com/IBM-Security/cloud-identity-keycloak-integration/pull/3)
