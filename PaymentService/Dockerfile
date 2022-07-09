FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} PaymentService.jar
ENTRYPOINT ["java","-jar","/PaymentService.jar"]
EXPOSE 8081