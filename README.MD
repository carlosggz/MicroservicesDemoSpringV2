# Microservices Demo using Spring Boot

Example of a microservices application using best practices:
- Service Discovery (Eureka)
- Config Server (Spring Cloud Config Server)
- Services communication using messaging (Spring Boot Stream)
- Api Gateway to control security and service access (Spring Cloud Gateway)
- Traceability (using [Zipkin](https://zipkin.io/))
- External security provider (using [Keycloak](https://www.keycloak.org/))

## Instructions

To execute the application you need several external services:
- Postgresql (Movies API)
- Mongodb (Actors API)
- RabbitMQ (for messaging)
- Keycloack (for security access)
- Zipkin (for traceability)

All these services are available on a docker compose file on folder scripts/dev. 

All services use a config server, containing the configuration settings for each environment. You can check it at:

    https://github.com/carlosggz/config-server-demo

## How to run

Clone the repository and open the project on your preferred IDE. Then, you must run the external services, available on a composer file on folder scripts/dev. Just run:

    docker-compose up --build

The security is handled by Keycloack and the composer already created the needed realm and related configuration, but you need to add the user. The needed script is already on scripts/auth/runonce(.sh or .bat, depending of your OS). Just execute it and the user will be added. The credentials are demo-user/demo-pass. You can check it works using a curl command:

    curl --location --request POST 'http://localhost:9000/realms/demo-realm/protocol/openid-connect/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'client_id=demo-client' \
    --data-urlencode 'client_secret=this-is-my-secret' \
    --data-urlencode 'username=demo-user' \
    --data-urlencode 'password=demo-pass' \
    --data-urlencode 'scope=profile'

The result will be a json containing a security token.

Next, you can run the projects using your IDE. The first service to run, must be the Eureka server, then the config server, and then the rest of the services.

Projects movies-api and actors-api, offers a Swagger UI to test them directly on the browser:

    movies-api: http://localhost:8081
    actors-api: http://localhost:8082

To access using the api-gateway, first you must log in and then access through it. There are a postman collection available at scripts/postman (with the environment file), that you can import and use. The first method is to log in which will save the token as a variable and in this way you can test the rest of the methods.

The postman collection can be executed too, at command line using newman (you need nodejs installed for it). There is already a script available for it. So, you can start the services and run the script to see if everything is working correctly.

To check interactions between services and requests/responses, you can access to the zipkin server at:

    http://localhost:9411

## Run everything

If you want to run all services from command line, you can use the script at folder scripts/run (you need nodejs installed for it). The script will compile all projects and copy the output jars, then you must run the composer on the same folder as the script will indicate you.

