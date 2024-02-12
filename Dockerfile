FROM URIHERE/buildpack-deps:bullseye  AS config

ARG NEXUS_USERNAME
ARG NEXUS_PASSWORD

ENV YELLOW_TEXT='\033[33m'
ENV BLUE_TEXT='\033[34m'
ENV RESET_TEXT='\033[0m'

RUN echo -e "${BLUE_TEXT}}First Stage Build${RESET_TEXT}"

# Download certificates using wget
RUN echo -e "${YELLOW_TEXT}Downloading certificates from <url> using wget${RESET_TEXT}"
RUN wget --no-check-certificate -r -np -nd -R "index.html*" URIHERE/wcf/latest/crt/ -P /usr/local/share/ca-certificates/WCF
RUN wget --no-check-certificate -r -np -nd -R "index.html*" URIHERE/dod/latest/ -P /usr/local/share/ca-certificates/WCF

# Update ca-certificates
RUN echo -e "${YELLOW_TEXT}Updating ca-certificates${RESET_TEXT}"
RUN update-ca-certificates

# Grabs MAVEN proxy
RUN mkdir -p /root/.m2
RUN wget --no-check-certificate -O /root/.m2/settings.xml URIHERE/config/maven/settings.xml
RUN echo -e "${YELLOW_TEXT}Updating Maven settings.xml with NEXUS credentials${RESET_TEXT}"
RUN sed -i.bak "s/<username>nexus_username<\/username>/<username>$NEXUS_USERNAME<\/username>/g" /root/.m2/settings.xml
RUN sed -i.bak "s/<password>nexus_password<\/password>/<password>$NEXUS_PASSWORD<\/password>/g" /root/.m2/settings.xml

# Grabs alpine proxy
RUN wget --no-check-certificate -O /usr/local/share/repositories URIHERE//config/alpine/alpine_16-sources.list
RUN sed -i "s/<username>/$NEXUS_USERNAME/g" /usr/local/share/repositories
RUN sed -i "s/<password>/$NEXUS_PASSWORD/g" /usr/local/share/repositories

# Use a base image that includes the necessary tools (e.g., curl, unzip)
FROM URIHERE/amazoncorretto:17-alpine-jdk AS build

ENV YELLOW_TEXT='\033[33m'
ENV BLUE_TEXT='\033[34m'
ENV RESET_TEXT='\033[0m'


RUN echo -e "${BLUE_TEXT}Second Stage Build${RESET_TEXT}"

COPY --from=config /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/ca-certificates.crt
COPY --from=config /usr/local/share/ca-certificates/WCF /usr/local/share/ca-certificates/WCF
COPY --from=config /etc/ssl/openssl.cnf /etc/ssl/openssl.cnf
COPY --from=config /usr/local/share/repositories /usr/local/share/repositories
COPY --from=config /root/.m2/settings.xml /root/.m2/settings.xml

COPY certs.sh .
RUN chmod +x certs.sh
RUN ./certs.sh

# Echo statement for ARGs
RUN echo -e "${YELLOW_TEXT}Setting ARGs for environment variables${RESET_TEXT}"

ENV GITLAB_ACCESS_TOKEN=$GITLAB_ACCESS_TOKEN
ENV GITLAB_URL=$GITLAB_URL

# COMMENTED OUT SINCE NO LONGER NEED MAVEN. TO INCLUDE UNCOMMENT BLOCK ON LINE 19 AND ADD COPY COMMAND
# RUN apk add maven
# RUN mvn dependency:purge-local-repository
# RUN ./gradlew build --refresh-dependencies
# RUN ./gradlew clean verify

# # Doing a two stage build like this helps eliminate vulnerablilite that may come along with build tools.
WORKDIR application
COPY . .

RUN echo -e "${YELLOW_TEXT}Cleaning Build and Pull Depenencies...${RESET_TEXT}"
# RUN ./gradlew clean
# RUN ./gradlew --refresh-dependencies

RUN echo -e "${YELLOW_TEXT}Building application using Gradle...${RESET_TEXT}"
# RUN ./gradlew tasks
RUN ./gradlew --no-daemon bootJar

RUN echo -e "${YELLOW_TEXT}Copying and extracting application JAR using jarmode...${RESET_TEXT}"
RUN cp build/libs/mmsri-4.0.18.jar app.jar

RUN ls -lah

#RUN apk add --no-cache findutils
#RUN find . -type f -name mms*.jar -not -iname '*javadoc*' -not -iname '*sources*' exec cp '{}' 'app.jar' ';'
#RUN find . -type f -name 'mms*.jar' -not -iname '*javadoc*' -not -iname '*sources*' -exec cp '{}' 'app.jar' ';'
RUN java -Djarmode=layertools -jar app.jar extract

RUN ls -lah

#make folder and copy project
#RUN mkdir /project
#COPY . /project
#WORKDIR /project

# NOT NEEDED UNLESS WE USE MAVEN
# RUN mvn clean package

FROM URIHERE/amazoncorretto:17-alpine-jdk as app_runner

ENV YELLOW_TEXT='\033[33m'
ENV BLUE_TEXT='\033[34m'
ENV RESET_TEXT='\033[0m'

RUN echo -e "${BLUE_TEXT}Third Stage Build${RESET_TEXT}"

COPY --from=config /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/ca-certificates.crt
COPY --from=config /usr/local/share/ca-certificates/WCF /usr/local/share/ca-certificates/WCF
COPY --from=config /etc/ssl/openssl.cnf /etc/ssl/openssl.cnf
COPY --from=config /usr/local/share/repositories /usr/local/share/repositories

# Re add certs
COPY certs.sh .
RUN chmod +x certs.sh
RUN ./certs.sh

# Create self signed url for
#RUN apk --no-cache add openssl
#RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/key.pem -out /etc/ssl/certs/cert.pem -subj "/CN=openmbee-mms.openmbee.svc.cluster.local"

RUN echo -e "${YELLOW_TEXT}Upgrading apk & installing procps${RESET_TEXT}"
RUN apk upgrade -q && apk add -q procps

RUN ls -lah

# RUN tree || true
WORKDIR application
RUN ls -lah

COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./
#COPY --from=build ./ ./app.jar
#COPY --from=build build/libs/mmsri-4.0.18.jar ./build/libs/mmsri-4.0.18.jar
#CMD ["java","-jar","/build/libs/mmsri-4.0.18.jar"]
#EXPOSE 8080

#Java Commands from OpenMBEE MMSRI
ENV JDK_JAVA_OPTIONS "-XX:MaxRAMPercentage=90.0 -XX:+PrintFlagsFinal -XX:+UseZGC"
#ENV SPRING_PROFILES_ACTIVE=test
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 5000
# COPY --from=application-builder application/dependencies/ ./
# COPY --from=application-builder application/spring-boot-loader/ ./
# COPY --from=application-builder application/snapshot-dependencies/ ./
# COPY --from=application-builder application/application/ ./

# # These commands update and upgrade the underlying os of the container, thus helping to eliminate os level vulnerabilites.
# # RUN apk upgrade
# EXPOSE 8080
# # CMD ["java","-jar","/project/target/colosseo-api-toolbelt-0.0.1-SNAPSHOT.jar"]
# CMD ["java","-jar","app.jar"]
