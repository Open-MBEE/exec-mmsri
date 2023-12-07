# Table of Contents

- [Overview](#overview)
    - [Getting Started:](#getting-started)
    - [Setting Up:](#setting-up)
    - [Local Development](#local-development)
        - [Local Instance Setup](#local-instance-setup)
        - [Developing With Docker](#developing-with-docker)
- [DevSecOps](#devsecops)
    - [Dockerfile](#dockerfile)
    - [GitLab CI/CD](#gitlab-cicd)
        - [GitLab CI](#gitlab-ci)
        - [GitLab CD Pipeline](#gitlab-cd-pipeline)
    - [OpenShift](#openshift)
- [Bottom Line Up Front:](#bottom-line-up-front)
    - [Solutions](#solutions)
        - [Solution 1: Use KONG API Gateway Proxy](#solution-1-use-kong-api-gateway-proxy)
        - [Solution 2: Use Ingress to create Reverse Proxy](#solution-2-use-ingress-to-create-reverse-proxy)
        - [Solution 3: Use KeyCloak Integration](#solution-3-use-keycloak-integration)
        - [Solution 4: Use Combination of the Previous Solutions](#solution-4-use-combination-of-the-previous-solutions)
        - [Solution 5: Use LDAP Authentication](#solution-5-use-ldap-authentication)
        - [Solution 6: Use TeamWork Cloud](#solution-6-use-teamwork-cloud)
- [Resources](#resources)

# Overview

This project is an implementation of NASA JPL's OpenMBEE View Editor(VE) and Model Management System (MMS). Note: When developing this application I used VE v4.0.2 and MMSRI v4.0.18. The MMSRI is a wrapped up version of the MMS, meaning instead of having all the subprojects in the codebase is uses fetches them from the Maven Repository instead. I have implemented VE v5.0 but OpenMBEE is still working on a release candidate, when that has been provided I will provide steps for setting that up.

**Documentation:**

- https://mms-reference-implementation.readthedocs.io/en/latest/index.html
- https://docs.openmbee.org/projects/ve/en/support-4.x/
- https://docs.openmbee.org/projects/ve/en/latest/

## Getting Started:

---

You will need the following:

- **Software:**
    - code editor(VS Code) [VS Code](https://code.visualstudio.com/download)
    - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=mac)
    - [JDK-17 download](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
    - [Docker Desktop](https://docs.docker.com/get-docker/)
    - Possibly Need to Install: Python2, Ruby, and Sass (You will know if it needed after step 4)
- **Accounts & Services**
    - OpenShift Namespaces
    - Nexus Repository
    - Keycloak Proxy
    - DNS route for VE
    - Nexus Credentials
    - TwistLock Credentials
    - KONG API Gateway Namespace

**Architecture**:
Provided in the codebase is a screenshot of the architecture design. The only difference is this diagram is based on Alfresco, a Content Management System

## Setting Up:

---

Provided below is the high level overview of the steps and processed needed to get the application up and running

1. **Install required software mentioned above**
2. **Clone Code vs Fork Code**
    1. Clone code from source code repositories if you are planning on committing back to the OpenSource, Fork the repository if you do not expect to commit back to the OpenMBEE project
    2. It may ask for SSH keys, refer to [Add SSH Key GitHub](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)
    3. MMS - `git clone <url to repo>`
    4. VE - `git clone <url to repo>`
    5. ElasticSearch - `git clone <url to repo>`
3. **MMS Setup (Back End)**

    - Basic Steps for this are also outlined here [MMSRI](https://github.com/Open-MBEE/mmsri)

    1. Docker Containers
        - Docker Compose
            1. Using docker run `docker-compose up --build` this will build you an elasticsearch, minio, and postgres image all wrapped up in 1 container
            2. When you want to stop using your project simply open Docker Desktop and click the stop button
            3. More information will be provided in the MMS Setup Notes, [MMSRI](https://github.com/Open-MBEE/mmsri)
        - Individual Docker **(Preferred)**
            1. Run Postgres Image (Required)
                - `docker run -d -e POSTGRES_PASSWORD=test1234 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 postgres:11-alpine`
            2. Run ElasticSearch Image (Required)
                - **Due to the vulnerabilities that are present with 7.8.1 the "safe" container is to use 8.10.2. I had to create a separate project that uses this version, setup steps for this will be following**
                - Steps:
                    - Run this command first to get local setup running `docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.8.1  `
                - **ONCE LOCAL SETUP IS RUNNING STOP PREVIOUS CONTAINER AND RUN THE FOLLOWING**
                - `docker build -t openmbee-elasticsearch:8.10.2 -f LocalDocker.Dockerfile .`
                    - builds the docker image
                    - LocalDocker.Dockerfile is a copy of the Dockerfile we use for the pipeline but without the first stage build
                - `docker run -d --name openmbee-elasticsearch openmbee-elasticsearch:8.10.2`
                    - runs our docker container
                - `docker cp example.txt your_container_name:/app/`
                - **VERIFY elasticsearch.yml was copied into image**
                    - open Docker Desktop, navigate to the terminal of the image.
                        - click the far right link in the table "openmbee-elasticsearch"
                        - if not in the directory already run cat /usr/share/elasticsearch/config/elasticsearch.yml
                        - VERIFY the contents match the file from the codebase
                    - **IF the file doesn't match** run the following:
                        - `docker cp elasticsearch.yml openmbee-elasticsearch:/usr/share/elasticsearch/config/`
            3. Run Minio Image (Optional)
                - `docker run -p 9000:9000 -e "MINIO_ACCESS_KEY=admintest" -e "MINIO_SECRET_KEY=admintest" minio/minio server /data`
    2. MMS Application
        - Run `./gradlew bootRun` which will run the application using Spring Boot, meaning it will run the project within a JVM(Java Virtual Machine)
            - to understand where this comes from view the `build.gradle` file
        - If you have IntelliJ IDEA setup you should be able to simply open the MMSRIApplication.java file then next to the debug button make sure the dropdown says "Current File" then click the Debug or Run button

4. **VE Setup (Front End)**
    1. To get the front end started install the code, then run `npm i`
    2. `npm i -g grunt-cli`
    3. The node sass commands can be run if you receive an error that says "Loading node-sass blah blah", if not skip ahead.
        1. `npm install node-sass@4.14.1`
        2. `npm update && npm install`
        3. `node ./node_modules/node-sass/scripts/install.js`
        4. `npm rebuild node-sass`
    4. Test if gruntfile and necessary software is installed run the following: `grunt build --verbose`
    5. If grunt file fails due to mismatching node versions or incompatibility issues with node and the packages please test out these commands:
        1. Test 1:
            1. `npm install grunt-contrib-concat grunt-contrib-uglify grunt-contrib-sass grunt-contrib-watch grunt-html2js --save-dev`
            2. `rm -rf package-lock.json (also changed the package.json)`
            3. `npm i sass`
            4. `npm i grunt-contrib-sass --save-dev`
            5. `npm i`
            6. `npm update`
            7. `yarn add @angular/cli` if you don't have yarn installed, run `npm i -g yarn`
            8. `nvm use default` (in this case its node version 19.0)
                - NVM is node version manager it helps with switching and installing between the different version of NodeJS, to install, refer to [Install NVM](https://www.freecodecamp.org/news/node-version-manager-nvm-install-guide/)
            9. `yarn add @angular/cli `
            10. `nvm use 8 `
            11. `grunt --verbose`
            12. `nvm use 15`
            13. `grunt --verbose`
        2. Test 2:
            1. `nvm use default`
            2. `npm i`
            3. `nvm use 8`
            4. `grunt --build`
        - Please refer to the following for an understanding of the Grunt Commands
    6. Install serve module which allow you to run a local web server for your application, `npm i -g serve`
    7. To debug an issue you will run `grunt build --env=<environemnt> && cd dist && serve`
        - **Local**: `grunt build --env=local && cd dist && serve`
            - urls will be pointing to APIs at localhost:8080/
        - **Dev**: `grunt build --env=development && cd dist && serve`
            - urls will be using kong proxy api
5. **MDK Setup**
    1. As of the development of this project there was no testing with MDK, to install and setup MDK refer to [MDK](https://github.com/Open-MBEE/mdk).
    - This is important due to the VE functionality being rather limited without the DocGen capabilities that comes from the MDK.
6. **Local Development**
    1. One great thing about using MMS is we are allowed to use Swagger-UI on local. This is something that will be blocked in any DoD environment on NMCI network but allowed for local development.
    2. When you spin up the Backend the MMS image will spin up a certain port, this will look something like `:8080`, you will need to change the code in the VE to point to this URL, you should be looking for config.example.js underneath app/config/config.example.json
        - You can rename the file to `config.<environment>.js` you just need to keep that in mind when we get to the Dockerfile and when running Grunt commands.
            - For example, you can create a config.local.js that points to your localhost and a config.production.js that points to you OpenShift Services, then when you run your Grunt Command you can run
    3. Now you should be able to spin up the project up, additional steps will need to preformed, so we aren't completely done yet.

## Local Development

---

### Local Instance Setup

Once the application is running, you should be able to navigate to the following:

- http://localhost:8080/v3/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/
- http://localhost:8080/actuator/health

Once you are on the login page you should be able to log in using the admin username and password that is set with `mms.admin.username` and `mms.admin.password`. Currently, these are set to `test`.

Another thing to note for generate a JWT secret for you application run the following command

- `node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"`

IF you still a red banner that says "Timed Out", please do the following:

- open Swagger UI
- find `createAuthenticationToken` under the Auth section of the endpoints near the top of the page.
- click 'Try It Out' and enter the request body next to username and password the values you have for mms.admin.username and mms.admin.password
- Click Execute and copy the token value
- Navigate to the top right and click 'Authorize' and past the token next to bearerToken
- Fill in username and password as well and click 'Log In'
- Now once you have this running, change the elasticsearch image to our custom elasticsearch image.

**Final Steps:**

- Once you log in you will need to create an Organization and a Project without it, you will log in and see "No Organizations Selected" and "No Projects Selected". So first step is use the Swagger UI to create an Organization, then create a Project for that corresponding Organization.

**Things to be Aware of:**

- If elasticsearch isn't running you will see your Organization populate but not your project
- Without the MDK running or setup your capabilities are limited and bugs exist in the VE due to this.
- **NOTE:** by default the application will run over http, to change this to **HTTPS** add the following lines to your application.properties file within the MMS. This can be found in `src/main/resouces/application.properties`:
    1. **Lines 15-25:** Need to double-check this.
        - Refer to the following documentation for SSL on MMS: [SSL/HSTS Documentation](https://mms-reference-implementation.readthedocs.io/en/latest/configuration.html#ssl-hsts)
        - Here are steps used to implement SSL on the server: [Implementing SSL on Server](https://www.thomasvitale.com/https-spring-boot-ssl-certificate/)
        - Add/modify the following lines of code:
          ```groovy
          #server.port=5000
          #server.forward-headers-strategy=framework
          server.ssl.enabled=true
          ##server.ssl.key-alias=yourkeystorealias
          server.ssl.key-store=classpath:mms.p12
          server.ssl.key-store-password=mms_password
          server.ssl.key-store-type=PKCS12
          ```
    2. Change your `config.<environment>.js` in VE to use https instead of http
    3. Running Elasticsearch over HTTPS is also necessary. **NOTE:** This is where the custom OpenMBEE image is utilized. For more insight into this, please refer to:
        - [Installing ElasticSearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
        - [Encrypting ElasticSearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/security-basic-setup-https.html)
        - Add/modify the following lines of code:
          ```groovy
          # Elasticsearch config
          elasticsearch.host=localhost
          elasticsearch.port=9200
          elasticsearch.http=https
          elasticsearch.limit.result=10000
          elasticsearch.limit.term=1000
          elasticsearch.limit.scrollTimeout=1000
          elasticsearch.limit.get=10000
          elasticsearch.limit.index=5000
          elasticsearch.limit.commit=100000
          ```
    4. Re-run applications

### Developing With Docker

These applications are meant to be ran and deployed within Docker images. One thing discovered while working on this application and configuring to compile and deploy through the pipeline is that you will run into compilation errors due to image missing commands, improper permissions, or ssl communication errors. So one way to prepare for this is to test your docker files locally. I have provided the steps below for testing your docker files locally.

1. (Optional) Create a new dockerfile called `LocalDocker.Dockerfile`
    1. Copy and Paste contents of `Dockerfile.Dockerfile` into `LocalDocker.Dockerfile`
2. Build docker image
    - `docker build -t <image_name>:<image_tage> -f LocalDocker.Dockerfile .`
3. Run docker image
    - `docker run -d --name <image_name> <image_name>:<image_tag>`
4. If you need to copy files into the image run the following:
    - `docker cp <file or folder path> <image_name>:<path_to_files>`

**Additional Information**

- If you would like to output the build steps to a file for debugging use the following build command:
    - `docker build -t <image_name>:<image_tage> -f LocalDocker.Dockerfile . > build_output.log 2>&1`

# DevSecOps

---

**Overview:** Outlined in this section is the DevSecOps process and an understanding on how to use it. Documentation covering all aspects of services and How-To's can be found here [NAVAIR CI/CD Documentation](https://documentation.apps.arena-workspace.navair.navy.mil/colosseo/).

**Responsibility Barrier**

| Task                                  | Developer | CI/CD Team | Container Team |                                            Notes                                            |
| ------------------------------------- | :-------: | :--------: | :------------: | :-----------------------------------------------------------------------------------------: |
| Create KeyCloak Account               |           |     ✅     |                |                                                                                             |
| Create KeyCloak SideCar               |           |            |       ✅       |                                                                                             |
| Create KeyCloak SideCar Routes        |           |            |       ✅       |                                                                                             |
| Create Nexus Registry                 |           |     ✅     |                |                                                                                             |
| Create Nexus Credentials              |           |     ✅     |                |                                                                                             |
| Setup Nexus Credentials in GitLab     |           |     ✅     |                |                                                                                             |
| Create TwistLock Credentials          |           |     ✅     |                |                                                                                             |
| Setup TwistLock Credentials in GitLab |           |     ✅     |                |                                                                                             |
| Create .gitlab-ci.yml                 |    ✅     |            |                |                                                                                             |
| Update GitLab CI/CD Variables         |    ✅     |     ✅     |                | Depending on permission and sometimes they will need to create variables at the Group level |
| Create OpenShift NameSpace            |           |            |       ✅       |                                                                                             |
| Create DeploymentConfigs              |    ✅     |            |                |                                                                                             |
| Create Secrets in OpenShift           |    ✅     |            |                |                                                                                             |
| Create ConfigMaps in OpenShift        |    ✅     |            |                |                                                                                             |
| Create Services in OpenShift          |    ✅     |            |                |                                                                                             |
| Create Routes in OpenShift            |           |            |       ✅       |                                                                                             |
| Create Ingress in OpenShift           |           |            |       ✅       |                                                                                             |
| Create PersistenceVolume in OpenShift |    ✅     |            |                |                                                                                             |
| Create KONG NameSpace                 |           |            |       ✅       |                                                                                             |
| Create KONG Consumer                  |           |            |       ✅       |                                                                                             |
| Create KONG Service                   |    ✅     |            |                |                                                                                             |
| Create KONG Route                     |    ✅     |            |                |                                                                                             |
| Create KONG ACL                       |           |            |       ✅       |                                                                                             |

## Dockerfile

When looking at the Dockerfiles you will notice they are multiple stages (each `FROM` line is a new stage). The dockerfiles are structured in such a way that:

- **Stage 1: configuration**
    - **This stage is a requirement for all images in NAVAIR**
    - Here we gather any certificates, setup proxies, and other things we will need for configuration the image.
    - credentials for SSL communication within the image
    - apk repository to be able to allow us to run `apk add` commands
    - Use Maven Repository
- **Stage 2: building application**
    - copy credentials grabbed in stage 1 and runs cert.sh which uses java's keytool command to update credentials and certificates
    - copy codebase files and creates a JAR file as our build artifact that is our project all wrapped into one file. This created under the build folder(`build/libs`) using `./gradlew --no-daemon bootJar`
    - Create a copy of the JAR file and
- **Stage 3: running/deploying application**
    - copy credentials grabbed in stage 1 and runs cert.sh which uses java's keytool command to update credentials and certificates
    - copies files from extract command
    - Compiles application and exposes at port 5000

## GitLab CI/CD

### GitLab CI

This is all setup within the `.gitlab-ci.yml` file. Here we are able to create the Application Pipeline aka Stage 4 of the CD Pipeline. By default, there needs to be a Build Stage and a Deploy Stage. This file will use the DevSecOps Parent Pipeline to build your application, SAST and container Scans. Without this file the pipeline will not be established and built. Within this file is also where the logic exists for generating the image tags for our container and sets up our connection to OpenShift that allows us to deploy to OpenShift.

**NOTE:** Only lines you should need to change is the first line in the file under variables where `SELECTOR` is declared on line 4. When creating deployment configs the SELECTOR should be the same name as your deployment config.

### GitLab CD Pipeline

In the DoD is a requirement to use a CI/CD pipeline for code, this allows for code, dependency, and container scanning. The most basic way to describe this workflow is to say. Whenever a commit is made the repository a pipeline is started. This pipeline is setup in roughly 4 stages.

1. Stage 1: Build DoD Dependencies
    - This will setup your Nexus Scans and decide what Code Scans will be performed in Stage 2
2. Stage 2: Build and Scan
    - Builds Container, then after a successful build a Nexus Container Scan will check for Vulnerabilities, if any are found it will provide a link to Prisma Cloud that will show you the vulnerabilities with the CVE, CVSS and feedback on what needs to be done to fix them (i.e. CVE-2016-1000027 CVSS 9.8 Spring Web 5.7, Fixed in 6.0)
    - **NOTE:**
        - No Critical Vulnerabilities are allowed, unless you work with IA and ISSM to request a "waiver". With a waiver you will have to provide evidence of a false positive. Then once that is provided, and you are approved you will need to provide a **Mitigation Plan** and a **Milestone** because you are only allowed **~ 30 days** to mitigate the vulnerability
        - High Vulnerabilities: These are allowed in Dev but must be resolved before going any farther(i.e Test, Stage, Prod)
    - Perform SAST Code Scanning based on what kinds of programming languages were found in the Stage 1 Scan

- Stage 3: Quality Gate
    - perform quality gate scanning and upload artifacts
- Stage 4: Application Pipeline
    - These are the steps that are defined in the `.gitlab-ci.yaml`
    - Here another container build is ran on success it will deploy application to the Nexus Repository
    - **NOTE(S):**
        - You will need to have your deployment configs created in OpenShift for the deployment to be successful.
        - If your branch has versioning number(VE-VERSION-4.0.2) as the branch name it will create an image tagged with it in Nexus (i.e. openmbee-ve:VE-VERSION-4.0.2)

## OpenShift

**Overview:** Arena or Container Platform runs on OpenShift which is built off of Kubernetes, some alternatives that people might be familiar with is AWS' EC2 services. In OpenShift, we deploy our applications and docker container to pods. In OpenShift there are a couple terms to know:

- **Pods**: These are application containers. One way to look at understanding the translation is for each docker image a OpenShift pod will be created as well. Pods are created through DeploymentConfig's which are created such that whenever a deployment is made a new pod is created.
- **DeploymentConfig** These are yaml files that setup what we want our Pod to be. Here you set the resource limits, url to container image that will be running within the pod, anything else needed for the application, or Secrets that you don't want to be displayed. You can also use ConfigMaps for environment variable based values. The 3rd thing is use PersistenceVolumes to store data through Pod deletion.
- **Services:** Sets up the networking between pods, this is where we can expose the pods to the web. Once these are setup by default all pods within the same cluster can communicate to each other. When created properly you will be provided with a hostname that can be used in other pods to reference these pods. For example this application uses an openmbee-mms, openmbee-ve, postgresql, and elasticsearch services to communicate.
- **Routes:** This is where you would attach URLs to your pods that can be used to access pods externally. Once a DNS ticket is created and if you are not using a KeyCloak SideCare you would create 2 routes for backend and frontend. Most application use KeyCloak embedded into the application
- **Ingress:** OpenShift Ingress is a crucial component that manages external access to the services in an OpenShift cluster, handling route traffic, and possibly SSL/TLS termination, ensuring that user requests are sent to the appropriate services. This is where you can create a reverse proxy that takes your front end which is on HTTPS and redirect the backend HTTP traffic to communicate over HTTPS with the application.
    - The current NASA JPL Team uses an ingress with their application that I will provide in the BLUF
- **Secrets:** Most of the values seen in secrets are autogenerated. Secrets can range from database username and password, Nexus Registry credentials or even TLS and SSL certificates
- **ConfigMaps** are where you would store your config file for keycloak, or your configuration for the backend url(for example how we have our config.dev.js and config.local.js). These ConfigMaps allow the code to be deployed to multiple environments without having to change the code base.

# Bottom Line Up Front:

---

**Overview:** The goal of this project was to stand up and deploy a working version of OpenMBEE's MMS and VE applications in OpenShift and add CAC Authentication to the application. We were told to pursue using KeyCloak SideCar, which acts as a Gateway Proxy for the application. This is the same as OAuth2 Proxy for those who are aware of what that is. This would in layman's terms when accessing the application send you to the proxy, verify CAC, then redirect to application. On the redirect the application would digest your information and create an account for you and leave the application authentication and protected with your CAC.

**RoadBlocks:**

- Vulnerabilities
- CI/CD Pipelines
- KeyCloak Proxy
- HTTPS vs HTTP Traffic
- ElasticSearch

As of the completion of the project, we were able to do the following:

- resolve all critical and high vulnerabilities
- integrate application with GitLab CI/CD pipeline
- Cohere to DoD Cyber Policy
- Create waiver for CVE-2016 1000027 and provide mitigation plan.
- Create custom docker container for VE, MMS, and ElasticSearch.
- Deploy to OpenShift

**Remaining Task:** CAC Authentication and ElasticSearch

**Bottom-Line:** Possible with multiple solutions

## Solutions

### Solution 1: Use KONG API Gateway Proxy

KONG API Gateway allows us to encrypt all traffic on the application with an API Key that cannot be intercepted and only accessed within the application. It also allows us to expose internal pods to the outside world by using the API Gateway. This would fix the original error we were getting with the SideCar. This would also fix the issue with our elasticsearch pod expecting HTTPS traffic but only receiving HTTP traffic. I had a solution for this elasticsearch issue but was unable to deploy. KONG Gateway also has a OAuth2 Authentication plugin that would allow us to use OAuth2 to authenticate all requests. This would lock the application down from exposed data or any unwanted attacks/access to critical mission data.

### Solution 2: Use Ingress to create Reverse Proxy

An Ingress would allow us to create a reverse proxy that would allow the internal pods to be able to communicate to the frontend running on HTTPS. This is how the NASA JPL team is able to get around the HTTPS vs HTTP communication. Especially since the issue with KeyCloak SideCar was running into the issue of HTTPS front end trying to communicate to the backend that was running on HTTP. We later created a DNS entry for the backend that allowed us to hit the backend over HTTPS, but we disabled KeyCloak Sidecar. This is where the 3rd issue occurred for the elasticsearch not communicating between HTTPS to HTTP. Again a solution for this was ready for deployment before being decommissioned but was unable to deploy. **But** since the backend would still be hosted over http elasticsearch will no longer complain about communicating over HTTPS vs HTTP.

The ingress file that the OpenMBEE team is using is the following:

```
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
 annotations:
   field.cattle.io/publicEndpoints: >-
     [{"addresses":[""],"port":80,"protocol":"HTTP","serviceName":"mms4-uat:cae-mms4-uat-l4","ingressName":"mms4-uat:cae-mms-uat","hostname":"cae-mms-uat.jpl.nasa.gov","path":"/","allNodes":false}]
 name: cae-mms-uat
 namespace: mms4-uat
spec:
 ingressClassName: nginx
 rules:
   - host: cae-mms-uat.jpl.nasa.gov
     http:
       paths:
         - backend:
             service:
               name: cae-mms4-uat-l4
               port:
                 number: 443
           path: /
           pathType: Prefix
status:
 loadBalancer:
   ingress:
     - hostname: >-
         k8s-ingressn-ingressn-blahblahblah.elb.us-gov-west-1.amazonaws.com
```

### Solution 3: Use KeyCloak Integration

When I am referring to KeyCloak Integration I am referring to integrating KeyCloak realms into the front end and the backend. Integrating into the front-end is not difficult but the backend is where it could be either easy or difficult depending on your understanding of JWT(JSON Web Tokens) and Authentication processes. The backend application currently uses its own Authentication subproject that runs off of JWT tokens, you could possibly find a way to integrate OAuth2 into the pre-existing Auth subproject OR rebuild the Authentication project from scratch to use KeyCloak. I have found multiple projects and articles of people setting this up themselves:

- https://github.com/ivangfr/springboot-react-keycloak/tree/master
- https://github.com/jannie-louwrens/spring-boot-keycloak-angular
- https://hamdi-bouallague.medium.com/secure-your-angular-spring-boot-application-using-keycloak-891efab50db8
- https://github.com/keycloak/keycloak-quickstarts/tree/latest/spring/rest-authz-resource-server
- https://www.keycloak.org/docs/latest/securing_apps/index.html
- https://www.keycloak.org/docs/latest/securing_apps/index.html#_java-servlet-filter-adapter

### Solution 4: Use Combination of the Previous Solutions

I throw this in as a solution because I did not have enough time to test out the ideas that I had, but it could be plausible that you will need a combination of the previously mentioned solutions to integrate CAC Authentication.

### Solution 5: Use LDAP Authentication

MMS supports LDAP and when looking inside the `application.properties` file they have values that can be setup to integrate LDAP authentication

### Solution 6: Use TeamWork Cloud

I am not sure if this is within the scope of the requirements provided to use but thought it is worth noting while working on the code I noticed in `application.properties` file that there is also values for setting TWC. I am not familiar with TWC but thought that should be noted as well.

**NOTE**: The version of elasticsearch that OpenMBEE is using is outdated and critically unsafe, so I had to create a custom container as mentioned earlier in this document.

We were guided toward using KeyCloak sidecar and this caused issues with our backend service not being reachable outside the pod. The way to fix this was to use KONG API to act as a proxy for the backend. This allowed us to secure the endpoints with a ApiKey allowing all requests to be secure and not vulnerable to attacks. Another thing found with the KONG API is there are plugins with OAuth2 credentials. So with father research we could have implemented this and provided a minimal working product that would have at least provided encrypted channels for the application.

# Resources

---

- https://ndiastorage.blob.core.usgovcloudapi.net/ndia/2018/systems/Wed_21466_Kruse.pdf
- https://www.sciencedirect.com/science/article/pii/S1877050919307392?ref=pdf_download&fr=RR-2&rr=813eaddf2bbb818c
- https://github.com/Open-MBEE
- https://github.com/Open-MBEE/mms
- https://github.com/Open-MBEE/mdk
- https://github.com/Open-MBEE/mmsri
- https://github.com/Open-MBEE/ve
- https://docs.konghq.com/hub/kong-inc/oauth2/
- https://documentation.apps.arena-workspace.navair.navy.mil/colosseo/
- https://github.com/ivangfr/springboot-react-keycloak/tree/master
- https://github.com/jannie-louwrens/spring-boot-keycloak-angular
- https://github.com/ivangfr/springboot-react-keycloak/tree/master
- https://github.com/jannie-louwrens/spring-boot-keycloak-angular
- https://hamdi-bouallague.medium.com/secure-your-angular-spring-boot-application-using-keycloak-891efab50db8
- https://github.com/keycloak/keycloak-quickstarts/tree/latest/spring/rest-authz-resource-server
- https://www.keycloak.org/docs/latest/securing_apps/index.html
- https://www.keycloak.org/docs/latest/securing_apps/index.html#_java-servlet-filter-adapter

### Last Word

**Document Creation and Knowledge Share:**
If there is any information missing or clarifications needed for this application feel free reach out to OpenMBEE POC Brandon Cratty, brandon.j.cratty2.civ@us.navy.mil
This document was created in Markdown language and used `pandoc` to convert from markdown to word document. To do so repeat the following:
1. Install pandoc, https://pandoc.org/installing.html
2. Install prettier document formatter, `npm install --global prettier`
3. Run Prettier, `prettier --write <document_name>.MD`
4. Convert using pandoc, `pandoc <document_name>.MD -o <document_name>.docx`