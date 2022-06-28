# MCA backend dev test
Products API using external [mocks module](https://github.com/dalogax/backendDevTest).
Only one endpoint is available, example:
```
http://localhost:5000/product/1/similar
```


## Run
It's possible to run the application using docker or natively.

### Run using docker
First, run the redis cache server:
```bash
sudo docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

then build yourapp image and run it:
```bash
sudo docker build -t mca_yourapp .
sudo docker run --network=host --name mca_yourapp_container mca_yourapp
```

If there are error there will be logged if option 'LOGS_ENABLED' is true in the code.
See logs from container ssh over it
```bash
sudo docker exec -it `sudo docker ps -a | grep mca_yourapp | cut -d" " -f1` /bin/bash
cat yourapp.log
```

### Run natively
These ports must be available:
- localhost:5000 -> API entrypoint
- localhost:6379 -> Redis in memory cache default port

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

Then run all the tests with:
```bash
./gradlew test
```

or only the unit tests:
```bash
 ./gradlew test --tests '*Test'
```
