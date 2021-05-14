plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.github.kawamuray.wasmtime:wasmtime-java:0.4.0")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("com.google.guava:guava:30.0-jre")
}

tasks.test {
    useJUnitPlatform()
}
