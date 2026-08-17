FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src/ src/
RUN ./mvnw -DskipTests package \
    && ./mvnw org.apache.maven.plugins:maven-dependency-plugin:3.9.0:copy \
        -Dartifact=com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11 \
        -DoutputDirectory=target/lib

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S lime && adduser -S lime -G lime \
    && mkdir -p /app/lib \
    && chown -R lime:lime /app

WORKDIR /app

COPY --from=build --chown=lime:lime /workspace/target/lime-*.jar app.jar
COPY --from=build --chown=lime:lime /workspace/target/lib/ lib/

USER lime

EXPOSE 8080

ENTRYPOINT ["java", "-Dloader.path=/app/lib", "-cp", "app.jar", "org.springframework.boot.loader.launch.PropertiesLauncher"]
