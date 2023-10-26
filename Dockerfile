FROM openjdk:17-jdk
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src/ src/
COPY .env .env
RUN ./mvnw package -DskipTests
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/target/DataFy-0.0.1-SNAPSHOT.jar"]
