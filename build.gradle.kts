plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "com.mylosoftworks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = "com.mylosoftworks"
            artifactId = "SerializeLib-kt"
            version = "1.0"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}