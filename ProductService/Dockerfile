FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ProductService.jar
ENTRYPOINT ["java","-jar","/ProductService.jar"]
EXPOSE 8080