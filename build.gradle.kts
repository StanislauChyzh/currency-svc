plugins {
    java
    id("org.liquibase.gradle") version "2.2.0"
    id("com.diffplug.spotless") version "6.25.0"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

val host = System.getenv("PG_HOST") ?: "localhost"
val port = System.getenv("PG_PORT") ?: "5433"
val database = System.getenv("PG_DATABASE") ?: "currency"
val dbSchema = System.getenv("PG_SCHEMA") ?: "public"
val dbUsername = System.getenv("PG_USERNAME") ?: "postgres"
val dbPassword = System.getenv("PG_PASSWORD") ?: "postgres"
val dbUrl = "jdbc:postgresql://$host:$port/$database?currentSchema=$dbSchema"

group = "com.test"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        target("src/*/java/**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat()
    }
}

buildscript {
    dependencies {
        classpath("org.liquibase:liquibase-core:4.25.0")
        classpath("org.liquibase:liquibase-gradle-plugin:2.2.0")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.5")

    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.18.1")

    implementation("org.apache.commons:commons-collections4:4.5.0-M2")

    runtimeOnly("org.postgresql:postgresql:42.7.4")
    implementation("org.liquibase:liquibase-core:4.30.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.30.0")

    compileOnly("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    testImplementation("org.testcontainers:postgresql:1.20.3")
    testImplementation("org.testcontainers:junit-jupiter:1.20.3")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.3.5")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.14")
    testImplementation("org.springframework:spring-webflux:5.3.39")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changelogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "url" to dbUrl,
            "username" to dbUsername,
            "password" to dbPassword,
            "driver" to "org.postgresql.Driver"
        )
    }
    runList = "main"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
