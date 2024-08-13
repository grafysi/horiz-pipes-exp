plugins {
    id("java")
}

group = "com.grafysi"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation(libs.debezium.api)
    implementation(libs.debezium.embedded)
    implementation(libs.debezium.connector.postgres)
    implementation(libs.log4j.slf4j.impl)
    implementation("io.confluent:kafka-connect-avro-converter:7.7.0")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}