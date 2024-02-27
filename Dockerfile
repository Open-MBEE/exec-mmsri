FROM openjdk:17.0.2-slim as build

WORKDIR application
COPY . .
RUN ./gradlew --no-daemon bootJar
RUN find . -type f -name mms*.jar -not -iname '*javadoc*' -not -iname '*sources*' -exec cp '{}' 'app.jar' ';'
RUN java -Djarmode=layertools -jar app.jar extract

FROM openjdk:17.0.2-slim
WORKDIR application
RUN apt-get update && apt-get install -y procps
COPY --from=build application/dependencies/ ./
COPY --from=build application/spring-boot-loader/ ./
COPY --from=build application/snapshot-dependencies/ ./
COPY --from=build application/application/ ./
ENV JDK_JAVA_OPTIONS "-XX:MaxRAMPercentage=90.0 -XX:+PrintFlagsFinal -XX:+UseZGC"
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080
