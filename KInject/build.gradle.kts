plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.mledoxvii"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
