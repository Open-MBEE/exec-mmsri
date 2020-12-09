FROM openjdk:15.0.1-jdk-slim as build

WORKDIR application
COPY . .
RUN ./gradlew --no-daemon bootJar
RUN cp build/libs/mms*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:15.0.1-jdk-slim
WORKDIR application
RUN apt-get update && apt-get install -y procps
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./
ENV JAVA_TOOL_OPTIONS "-XX:MaxRAMPercentage=90.0 -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:+UseStringDeduplication"
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080
