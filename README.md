# Project Setup

To use this project, first create an `env.properties` file in the same directory as `application.properties` with the following content:

```plaintext
POSTGRESQL_API=<JDBC link of PostgreSQL database>
DB_USER=<database username>
DB_PASSWORD=<database password>
SECRET_KEY=<JWT secret key>
EMAIL_ADDRESS=<email for password recovery functionality>
APP_PASSWORD=<application password for the above email>
```

# Build the Docker Image

```plaintext
docker build -t spring-boot-backend .
```

# Run the Docker Container

docker run -p 8080:8080 spring-boot-backend
