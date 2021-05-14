plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.11.1")

    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    compileOnly("org.projectlombok:lombok:1.18.12")

    api("org.apache.commons:commons-math3:3.6.1")
    api("io.github.kawamuray.wasmtime:wasmtime-java:0.4.0")

    implementation("com.google.guava:guava:30.0-jre")
}

tasks.test {
    useJUnitPlatform()
}
