# ![RealWorld Example App](spring-logo.png)

> ### DDD architecture implementation using Spring Boot (Spring Security, Spring Data, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully-fledged application built with [Spring](https://spring.io) including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to  the [RealWorld](https://github.com/gothinkster/realworld) repo.

# How it works

This application uses Spring Boot with Java 11. The implementation is meant to demonstrate idiomatic usage of Spring Framework and other modern Java technologies.

### Basic approach

The app is created using API-first approach. I.e. a swagger file (see the `api` directory) is used
to generate a skeleton of the REST API together with models (DTO) used for json representation.

In `pom.xml` you can see the usage of [openapi-generator-maven-plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin) plugin which performs actual code generation.

The app itself implements generated interfaces using DDD philosophy. See the structure below.

### Project structure:

```
application/            -> API of the app, encapsulates the domain
+-- service/            -> implementation of generated interfaces
+-- exception           -> application exceptions
+-- util                -> some helper classes including DTO mapping layer
domain/                 -> core business implementation layer
+-- aggregate/          -> aggregates are cluster of domain entities
+-- service/            -> doamin services that operate with domain entities
infrastructure/         -> technical details layer
+-- config/             -> dependency injection configuration
+-- security/           -> security configuration
```

### Some features of the project setup
* [Checker framework](https://checkerframework.org) prevents null-pointer exceptions
* Static code analysis with [Checkstyle](https://checkstyle.sourceforge.io), [PMD](https://pmd.github.io) and [Spotbugs](https://spotbugs.github.io) (see configs)
* Automatic code formatting with [fmt-maven-plugin](https://github.com/coveooss/fmt-maven-plugin)
* Checkstyle is configured to check for adherence to [Google Style](https://google.github.io/styleguide/javaguide.html)

# Getting started

### Start the local server

```bash
 make run
 ```

The server should be running at http://localhost:8080


### Running postman collection tests

```
make postman-test
```

### Building jar file

```
make
```


# What can be improved

* There is no command and query segregation, as the result, we can see a lot of JPA hacking
* [JEP 359 aka Java records](https://openjdk.java.net/jeps/359) is still a preview, but it could simplify the codebase
* `openapi-generator-maven-plugin` is not a perfect generator - it uses some old spring dependencies
* Test coverage can be improved
