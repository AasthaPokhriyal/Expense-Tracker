FROM openjdk:22-jdk
ADD target/expense-tracker.jar expense-tracker.jar
ENTRYPOINT ["java", "-jar", "/expense-tracker.jar"]
# WORKDIR /app

# COPY target/expense-tracker-0.0.1-SNAPSHOT.jar app.jar

# EXPOSE 8080

