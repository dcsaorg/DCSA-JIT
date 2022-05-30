FROM eclipse-temurin:17-jre-alpine

EXPOSE 9090
ENV db_hostname dcsa_db
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY jit-service/src/main/resources/application.yml .
COPY jit-service/target/dcsa-jit-service.jar .
CMD ["/run.sh"]
