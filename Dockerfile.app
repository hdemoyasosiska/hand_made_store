FROM gradle:jdk19 AS build

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN gradle build


FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/HM_store_for_RBD-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
