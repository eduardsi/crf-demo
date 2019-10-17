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
    maven {
        url = uri("http://repo.spring.io/libs-milestone")
    }
}

val springVersion = "2.2.0.M5"
val guavaVersion = "27.0.1-jre"

plugins {
    java
    jacoco
//    checkstyle
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("net.ltgt.errorprone") version "0.8.1"
}

group = "net.sizovs"
version = "UNSPECIFIED"

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}


// doesn't work in Java 13 yet
//checkstyle {
//    toolVersion = "8.23"
//}

tasks {
    "check" {
        dependsOn(jacocoTestCoverageVerification)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("TypeParameterUnusedInFormals")
        option("NullAway:AnnotatedPackages", "awsm")
        option("NullAway:ExcludedFieldAnnotations", "" +
                "org.mockito.Mock," +
                "org.springframework.beans.factory.annotation.Autowired"
        )
        option("NullAway:ExternalInitAnnotations", "" +
                "javax.persistence.Entity," +
                "javax.persistence.Embeddable,"
        )
    }
}
tasks.withType<Test> {
    maxParallelForks = 4
    timeout.set(Duration.ofMinutes(2))
    useJUnitPlatform()
}

//tasks.test {
//    extensions.configure(JacocoTaskExtension::class) {
//        excludes = listOf("$buildDir/generated")
//    }
//}

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

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.hashids:hashids:1.0.3")
    implementation("net.jodah:failsafe:2.3.1")
    implementation("com.h2database:h2:1.4.195")
    implementation("org.threeten:threeten-extra:1.5.0")
    implementation("org.iban4j:iban4j:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    implementation("org.springframework:spring-webflux:5.1.9.RELEASE")
    implementation("org.glassfish:javax.json:1.1.4")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("com.machinezoo.noexception:noexception:1.3.4")
    implementation("org.msgpack:jackson-dataformat-msgpack:0.8.17")
    implementation("com.github.ben-manes.caffeine:caffeine:2.5.0")
    implementation("one.util:streamex:0.7.0")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")


    testImplementation("com.github.tomakehurst:wiremock-jre8:2.25.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.assertj:assertj-core:3.9.1")
    testImplementation("org.awaitility:awaitility:4.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.1")
    testImplementation("com.github.javafaker:javafaker:1.0.0")
    testImplementation("org.hamcrest:java-hamcrest:2.0.0.0")
    testImplementation("com.google.guava:guava-testlib:$guavaVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5-engine:0.11.0")
    testImplementation("com.pivovarit:parallel-collectors:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}


