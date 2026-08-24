FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src/ src/
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S lime && adduser -S lime -G lime

WORKDIR /app

COPY --from=build --chown=lime:lime /workspace/target/lime-*.jar app.jar

USER lime

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
