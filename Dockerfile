FROM maven:3.8.5-openjdk-17 as BUILD
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/classes/com/project/shopapp/ShopappApplication.class Shopapp.jar
EXPOSE 1313
ENTRYPOINT ["java", "-jar"]
