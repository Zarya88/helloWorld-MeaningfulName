plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "ex1 demo for spring boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:6.2.0") //assignment5
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
