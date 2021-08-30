# DCSA OVS - Operational Vessel Schedule

Building and running manually/locally
-------------------------------------

1) Initialize your local postgresql database as described in [datamodel/README.md](https://github.com/dcsaorg/DCSA-Information-Model/blob/master/README.md), then

2) Build **DCSA-Core** as described in [DCSA-Core/README.md](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md), then

3) Build **DCSA-Event-Core** as described in [DCSA-Event-Core/README.md](https://github.com/dcsaorg/DCSA-Event-Core/blob/master/README.md), then

4) Build **DCSA-OVS**, ``mvn package``

5) Run application,
```
mvn spring-boot:run [options] 

options:
 -Dspring-boot.run.arguments="--DB_HOSTNAME=localhost:5432 --AUTH0_ENABLED=false"
```
or using docker-compose
```
docker-compose up -d -V --build
```

Check the running application,
```
curl http://localhost:9090/v2/actuator/health
```