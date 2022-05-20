FROM eclipse-temurin:17-jre-alpine

EXPOSE 9090
ENV db_hostname dcsa_db
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY src/main/resources/application.yaml .
COPY target/dcsa_jit-*.jar .
CMD ["/run.sh"]
