# DCSA JIT - Just In Time (Port Call optimization)

Building and Running the project,
-------------------------------------
**[RECOMMENDED]**
Setup a Github Personal Access Token as mentioned [here](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md#how-to-use-dcsa-core-packages), then skip to **step 3**.

If you would like to build required DCSA packages individually, begin with step 1.

1) Build **DCSA-Core** as described in [DCSA-Core/README.md](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md#to-build-manually-run), then

2) Build **DCSA-Event-Core** as described in [DCSA-Event-Core/README.md](https://github.com/dcsaorg/DCSA-Event-Core/blob/master/README.md#to-build-manually-run), then

3) Clone **DCSA-JIT** (with ``--recurse-submodules`` option.) and Build using, ``mvn package``

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


Testing all timestamps via postman/newman
-----------------------------------------

The repo includes a postman/newman collection for testing all
valid and some invalid combinations of timestamps and their
input.  The test input is generated via the script
`generate_pm_test_data.py` using data from the
`DCSA-Information-Model` submodule.

The flow is:

     # Start the JIT application (and have it listen to localhost:9090)
     $ python3 generate_pm_test_data.py iteration-data.csv
     $ newman run -d iteration-data.csv postman_collection.combinatorial.json

Note that the iteration-data by default covers 4500+ test cases of both
positive and negative test cases. Keep in mind that:

 * The test will generate a lot of timestamps (1900+ on a successful run)
 * The postman UI (at least at version `10.1.2`) may not handle the iteration
   data very gracefully performance-wise.  We recommend you use `newman`
   for running the tests as it has considerably less overhead.
 * You can split the iteration-data up into smaller parts.  E.g.:

       (head -n1 iteration-data.csv ; grep ,positive, iteration-data.csv) > only-positive.csv

   Will give you a file `only-positive.csv` with only the positive tests.

If a test fail, you can read the corresponding row in the `iteration-data.csv`
file to see what the test covered.  This includes the "timestamp name",
whether the test was positive (expected to be accepted) or negative (expected
to be rejected) as well as which fields are included/excluded.
