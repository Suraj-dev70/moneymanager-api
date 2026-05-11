FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v3.5.13.jar
EXPOSE 9020
ENTRYPOINT["java","-jar","moneymanager-v3.5.13.jar"]