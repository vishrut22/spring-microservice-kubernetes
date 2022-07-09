FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} OrderService.jar
ENTRYPOINT ["java","-jar","/OrderService.jar"]
EXPOSE 8082