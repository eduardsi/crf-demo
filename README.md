# Awesome Bank app

This is a demo enterprise Java application, built using some the best practices covered in Effective Java Software Design [course](https://devchampions.com/training/java). 

## Structure
```
unsuck-java/
└── application         # what the application does (api to the domain) with acceptance tests
    ├+─ awsm.domain.banking
    ├+─ awsm.domain.crm
    ├+─ ...    
└── domain              # what the application is (domain) with unit tests
    ├+─ awsm.domain.banking
    ├+─ awsm.domain.crm
    ├+─ ...
└── infrastructure      # supporting infra and middleware
└── database            # database migrations
```

## Tech stack
- [x] Spring for configuration and dependency injection
- [x] jOOQ for fluent data access
- [x] Flyway for database migrations
- [x] Failsafe for fault tolerance
- [x] PipelinR for service layer
- [x] Testcontainers for wrapping dependencies in Docker
- [x] Spock for testing
- [x] ArchUnit for enforcing architecture constraints

### Requirements
- [x] Docker
- [x] Java 12+

#### Running the app in dev mode
```
./gradlew build assemble bootDevRun -x test
```

#### Running tests
```
./gradlew clean test
```
