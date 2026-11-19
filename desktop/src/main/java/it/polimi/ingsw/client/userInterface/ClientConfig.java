package it.polimi.ingsw.client.userInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Default server address baked in at build time from the repo's client.properties
 * (see desktop/build.gradle.kts). Falls back to localhost:12345 if missing.
 */
public final class ClientConfig {
    private static final Properties PROPERTIES = load();

    private ClientConfig() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ClientConfig.class.getResourceAsStream("/client.properties")) {
            if (in != null) properties.load(in);
        } catch (IOException ignored) {
            // fall back to the defaults below
        }
        return properties;
    }

    public static String host() {
        return PROPERTIES.getProperty("server.host", "localhost");
    }

    public static int port() {
        try {
            return Integer.parseInt(PROPERTIES.getProperty("server.port", "12345").strip());
        } catch (NumberFormatException e) {
            return 12345;
        }
    }

    public static InetSocketAddress defaultAddress() {
        return new InetSocketAddress(host(), port());
    }
}
