plugins {
    id("java")
    `maven-publish`
}

group = "ua.edu.ukma"
version = "1.0-BETA"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
    // https://mvnrepository.com/artifact/com.google.truth/truth
    testImplementation("com.google.truth:truth:1.1.5")
    // https://mvnrepository.com/artifact/org.mockito/mockito-core
    testImplementation("org.mockito:mockito-core:5.5.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}