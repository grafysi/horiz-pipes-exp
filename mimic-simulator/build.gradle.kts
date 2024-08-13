plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "com.grafysi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.commons.csv)
    implementation(libs.slf4j.api)
    implementation(libs.postgresql)
    implementation(libs.commons.lang3)


    testImplementation(libs.log4j.slf4j.impl)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}