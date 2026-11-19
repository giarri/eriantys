plugins {
    java
    application
    jacoco
    alias(libs.plugins.javafx)
    alias(libs.plugins.shadow)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("it.polimi.ingsw.startUp.Main")
}

javafx {
    version = "21.0.13.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
    implementation(libs.log4j.legacy)
    compileOnly(libs.jetbrains.annotations)
    testCompileOnly(libs.jetbrains.annotations)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val clientConfig: Map<String, String> by rootProject.extra

// Bakes the configured server address into the jar as /client.properties.
tasks.processResources {
    val host = (findProperty("server.host") as String?) ?: clientConfig["desktop.server.host"] ?: "localhost"
    val port = (findProperty("server.port") as String?) ?: clientConfig["server.port"] ?: "12345"
    inputs.property("host", host)
    inputs.property("port", port)
    doLast {
        destinationDir.resolve("client.properties").writeText("server.host=$host\nserver.port=$port\n")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "it.polimi.ingsw.startUp.Main"
        attributes["Created-By"] = "AM01 - Giovanni Arriciati, Federico Arcelaschi, Lorenzo Aicardi"
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "it.polimi.ingsw.startUp.Main"
    }
}
