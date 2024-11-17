# currency-svc

currency-svc is a service that manages currency exchange rates.

# Get Started
## Prerequisite
* Java Version: JDK 21
* Docker, Docker-compose

## Build And Run
Start services which are required for local development.

* Run local backing services

```bash
docker system prune -a
docker-compose up -d 
 ```

* Stop local backing services

```bash
docker-compose down -v 
 ```

## Open API documentation

http://localhost:8080/swagger-ui/index.html
