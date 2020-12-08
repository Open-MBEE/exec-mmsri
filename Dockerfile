FROM openjdk:11.0.8-jdk as build

WORKDIR application
COPY . .
RUN ./gradlew --no-daemon bootJar
RUN cp build/libs/mms*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:11.0.8-jdk
WORKDIR application
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./
ENV JAVA_TOOL_OPTIONS "-XX:MaxRAMPercentage=90.0 -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseZGC"
ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080
