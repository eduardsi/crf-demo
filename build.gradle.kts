buildscript {
    repositories {
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}

val springVersion = "2.1.2.RELEASE"

plugins {
    java
    id("org.springframework.boot") version "2.1.2.RELEASE"
}


repositories {
    jcenter()
    mavenCentral()
}

group = "net.sizovs"
version = "UNSPECIFIED"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion") {
        exclude(module = "spring-boot-starter-tomcat")
    }

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("an.awesome:pipelinr:+")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("com.h2database:h2:1.4.195")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    implementation("com.google.guava:guava:27.0.1-jre")
    implementation("com.github.ben-manes.caffeine:caffeine:2.5.0")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.assertj:assertj-core:3.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.1.0")
}


