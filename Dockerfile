FROM openjdk:11
EXPOSE 8082
ADD target/DevOps_Project-1.0.jar DevOps_Project.jar
ENTRYPOINT ["java","-jar","DevOps_Project.jar"]