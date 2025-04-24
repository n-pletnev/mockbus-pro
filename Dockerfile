FROM gradle:8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM eclipse-temurin:21-alpine
COPY --from=build /home/gradle/src/build/libs/mockbus-pro-all.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
EXPOSE 5020