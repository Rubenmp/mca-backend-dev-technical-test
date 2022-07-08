# MCA backend dev test
Products API using external [mocks module](https://github.com/dalogax/backendDevTest).
Only one endpoint is available to fetch all the products similar to a given one.
For example, we can request similar products to the product with id 1 using:
```
http://localhost:5000/product/1/similar
```


## Run
It's possible to run the application using docker or natively.

### Run using docker
First, run the redis cache server:
```bash
sudo docker run --rm -p 6379:6379 redis/redis-stack-server:latest
# Another possibility is to run it in detached mode:
sudo docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

then build *yourapp* image and run it:
```bash
sudo docker build -t mca_yourapp .
sudo docker run --network=host --rm mca_yourapp
# Another possibility is to run it in detached mode:
sudo docker run --network=host -d --name mca_yourapp_container mca_yourapp
```

Errors will be logged if the variable 'LOGS_ENABLED' is true in the code.
See logs from container ssh over it
```bash
sudo docker exec -it `sudo docker ps -a | grep mca_yourapp | cut -d" " -f1` /bin/bash
cat yourapp.log
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
redis-cli FLUSHDB # If redis is installed
# In case of Redis executed by docker:
sudo docker ps # Search redis container id
docker exec -it <redis_container_id> bash # Connect to the redis container
redis-cli FLUSHDB # Run this command inside the container
```

## Tests
Name convention:
- Unit tests: **Test.java*
- Integration tests: **IT.java*

Integration tests require module Mocks (in project [*backendDevTest*](https://github.com/dalogax/backendDevTest)) to be running.
```bash
sudo docker compose up -d simulado influxdb grafana
```

Then run all the tests with:
```bash
./gradlew test
# or using docker:
sudo docker build -f DockerfileTest -t mca_yourapp_test .
sudo docker run --network=host --rm mca_yourapp_test
```

or only the unit tests:
```bash
 ./gradlew test --tests '*Test'
```
