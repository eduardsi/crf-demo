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
    groovy
    id("org.springframework.boot") version "2.1.2.RELEASE"
    id("io.franzbecker.gradle-lombok") version "1.14"
}

lombok {
    version = "1.18.4"
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

    implementation("com.h2database:h2:1.4.195")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    implementation("com.google.guava:guava:27.0.1-jre")
    implementation("com.github.ben-manes.caffeine:caffeine:2.5.0")
    implementation("javax.xml.bind:jaxb-api:2.3.0")

    testImplementation("org.spockframework:spock-core:1.3-RC1-groovy-2.5")
    testImplementation("org.spockframework:spock-spring:1.3-RC1-groovy-2.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.codehaus.groovy:groovy-all:2.5.5")
}

