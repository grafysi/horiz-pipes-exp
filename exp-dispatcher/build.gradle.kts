plugins {
    id("java")
}

group = "com.grafysi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.log4j.slf4j.impl)
    implementation(libs.debezium.api)
    implementation(libs.debezium.embedded)
    implementation(libs.debezium.connector.postgres)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}