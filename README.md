# DCSA JIT - Just In Time (Port Call optimization)

Building and running manually/locally
-------------------------------------

Initialize your local postgresql database as described in datamodel/README.md, then
```
export db_hostname=localhost
export DCSA_CORE_Version=0.7.11 #or whatever version is the right one
```
Then build and run with
```
mvn install:install-file -Dfile=../DCSA-Core/target/dcsa_core-$DCSA_CORE_Version.jar -DgroupId=org.dcsa -DartifactId=dcsa_core -Dversion=local-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn spring-boot:run -Ddcsa.version=local-SNAPSHOT
```
or using docker-compose
```
mvn package -Ddcsa.version=local-SNAPSHOT
docker-compose up -d -V --build
```

Then try and access the installation say on
```
http://localhost:9090/v1/events
```

Building and running using docker-compose
-----------------------------------------
To build using DCSA-core from GitHub packages
```
mvn package
docker-compose up -d -V --build
```
