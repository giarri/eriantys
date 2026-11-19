plugins {
    id("org.sonarqube") version "7.3.1.8318"
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Client server-address config: client.properties, overridden by the git-ignored
// client.local.properties, overridden by -Pserver.host / -Pserver.port.
val clientConfig: Map<String, String> by extra {
    val props = java.util.Properties()
    listOf("client.properties", "client.local.properties").forEach { name ->
        file(name).takeIf { it.exists() }?.reader()?.use(props::load)
    }
    props.stringPropertyNames().associateWith { props.getProperty(it).trim() }
}

allprojects {
    group = "it.polimi.ingsw"
    version = "3.0"
}
