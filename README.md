# MCA backend dev test
Products API using external [mocks module](https://github.com/dalogax/backendDevTest).
Only one endpoint is available to fetch all the products similar to a given one.
For example, we can request similar products to the product with id 1 using:
```
http://localhost:5000/product/1/similar
```


## Run
It's possible to run the application using docker or natively.
The errors will be logged in file *yourapp.log*.

### Run using docker
First, run the redis cache server:
```bash
sudo docker run --rm -p 6379:6379 redis/redis-stack-server:latest # Tested with Redis version 6.2.7
# Another possibility is to run it in detached mode:
# sudo docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

then build *yourapp* image and run it:
```bash
sudo docker build -f ./dockerfiles/DockerfileRun -t mca_yourapp .
sudo docker run --network=host --rm mca_yourapp
# Another possibility is to run it in detached mode:
# sudo docker run --network=host -d --name mca_yourapp_container mca_yourapp
```

See logs from container by ssh over it
```bash
sudo docker exec -it `sudo docker ps --format '{{.ID}}' --filter ancestor=mca_yourapp | tail -1` /bin/bash
cat yourapp.log
```

It is possible to remove cache data inside the redis container using
```bash
sudo docker exec -it `sudo docker ps --format '{{.ID}}' --filter ancestor=redis/redis-stack-server:latest | tail -1` /bin/bash
redis-cli FLUSHDB # Run this command inside the container
```


### Run natively
These ports must be available:
- localhost:5000 -> API entry point
- localhost:6379 -> Redis in-memory cache default port

Install:
- Java 17 (OpenJDK 17)
```bash
sudo apt update
sudo apt install openjdk-17-jre
sudo apt install openjdk-17-jdk
java --version # Verify installation
```
- Redis
```bash
sudo apt update
sudo apt install redis-server
redis-server --version # Verify installation
```

then run
```bash
./gradlew bootRun
```

It is possible to remove cache data using
```bash
redis-cli FLUSHDB
```

## Tests
Name convention:
- Unit tests: **Test.java*
- Integration tests: **IT.java*

Integration tests require module Mocks (in project [*backendDevTest*](https://github.com/dalogax/backendDevTest)) to be running.
```bash
sudo docker compose up -d simulado influxdb grafana
```

### Run tests with docker
Run all the tests with:
```bash
sudo docker build -f ./dockerfiles/DockerfileTest -t mca_yourapp_test .
sudo docker run --network=host --rm mca_yourapp_test
```

or only the unit tests:
```bash
sudo docker build -f ./dockerfiles/DockerfileUnitTest -t mca_yourapp_unit_test .
sudo docker run --network=host --rm mca_yourapp_unit_test
```


### Run tests natively
Run all the tests with:
```bash
./gradlew test
```

or only the unit tests:
```bash
./gradlew test --tests '*Test'
```
