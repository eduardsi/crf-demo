import net.ltgt.gradle.errorprone.errorprone
import java.time.Duration


buildscript {
    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://jitpack.io")
    }
}

val springVersion = "2.1.2.RELEASE"
val guavaVersion = "27.0.1-jre"

plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "2.1.2.RELEASE"
    id("net.ltgt.errorprone") version "0.8.1"
}

group = "net.sizovs"
version = "UNSPECIFIED"

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}


checkstyle {
    toolVersion = "8.23"
}

tasks {
    "check" {
        dependsOn(jacocoTestCoverageVerification)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("TypeParameterUnusedInFormals")
        option("NullAway:AnnotatedPackages", "awsm")
        option("NullAway:ExternalInitAnnotations", "" +
                "javax.persistence.Entity," +
                "javax.persistence.Embeddable," +
                "org.springframework.boot.test.context.SpringBootTest")
    }
}
tasks.withType<Test> {
    maxParallelForks = 4
    timeout.set(Duration.ofMinutes(2))
    useJUnitPlatform()
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion") {
        exclude(module = "spring-boot-starter-tomcat")
    }

    errorprone("com.google.errorprone:error_prone_core:2.3.3")
    errorprone("com.uber.nullaway:nullaway:0.7.5")

    annotationProcessor("org.hibernate:hibernate-jpamodelgen:5.4.4.Final")
    compileOnly("org.hibernate:hibernate-jpamodelgen:5.4.4.Final")

    implementation("org.zalando:faux-pas:0.8.0")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.hashids:hashids:1.0.3")
    implementation("com.github.jhalterman:failsafe:master") // Timeouts coming in 2.2.0
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.h2database:h2:1.4.195")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:2.5.0")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")


    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.assertj:assertj-core:3.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.1")
    testImplementation("com.github.javafaker:javafaker:1.0.0")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("com.google.guava:guava-testlib:$guavaVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5-engine:0.11.0")
    testImplementation("com.pivovarit:parallel-collectors:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}


