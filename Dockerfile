FROM openjdk:21-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon -x test

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/BankPaymentProviderApp-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]