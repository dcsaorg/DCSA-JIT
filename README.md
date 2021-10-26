# DCSA OVS - Operational Vessel Schedule

![DCSA-OVS MASTER](https://github.com/dcsaorg/DCSA-OVS/actions/workflows/master.yml/badge.svg?branch=master) ![DCSA-OVS DEV](https://github.com/dcsaorg/DCSA-OVS/actions/workflows/dev.yml/badge.svg?branch=dev)

------------------------------------------------------------------------------------------------------------------------

### BUILDING AND RUNNING THE PROJECT

>**[RECOMMENDED]** Set up a Github Personal Access Token (PAT) as mentioned [here](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md#how-to-use-dcsa-core-packages), then skip to **step 3**.

If you would like to build required DCSA packages individually, begin with step 1.

1) Build **DCSA-Core** as described in [DCSA-Core/README.md](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md#to-build-manually-run), then

2) Build **DCSA-Event-Core** as described in [DCSA-Event-Core/README.md](https://github.com/dcsaorg/DCSA-Event-Core/blob/master/README.md#to-build-manually-run), then

3) Clone **DCSA-OVS** (with ``--recurse-submodules`` option.) and Build using, ``mvn package``

4) Initialize your local postgresql database as described in [datamodel/README.md](https://github.com/dcsaorg/DCSA-Information-Model/blob/master/README.md) \
   or If you have docker installed, you may skip this step and use the docker-compose command mentioned below to set it up (This will initialize the application along with the database).

5) Run application,
```
mvn spring-boot:run [options] 

options:
 -Dspring-boot.run.arguments="--DB_HOSTNAME=localhost:5432 --LOG_LEVEL=DEBUG"
```

OR using **docker-compose**

```
docker-compose up -d -V --build
```

6) Verify if the application is running,
```
curl http://localhost:9090/v2/actuator/health
```
------------------------------------------------------------------------------------------------------------------------
### DEVELOPMENT FLOW

We maintain two branches, `master` and `dev`. \
`master` is always stable and updated with ongoing development (provided it's stable) at the end of every sprint.

Development continues on `dev` and feature branches are created based on `dev`.

A typical development flow would look like:

1) Create feature branch with `dev` as base.
2) Raise PR against `dev`, dev CI validates the PR ensuring everything is fine.
3) Merge with dev.
4) At the end of a sprint, we sync the core dependencies (`dev-<project>`) with their respective `master`,\
   update the dependency versions in `dev` and merge with `master` after successful CI validation.
5) Continue development on `dev` for new sprint.
