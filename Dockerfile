FROM openjdk:17-jdk

COPY /target/service1-0.0.1-SNAPSHOT.jar app/service1-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","app/service1-0.0.1-SNAPSHOT.jar"]