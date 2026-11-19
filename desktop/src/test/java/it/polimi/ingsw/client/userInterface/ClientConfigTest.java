package it.polimi.ingsw.client.userInterface;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientConfigTest {

    @Test
    void hostIsNeverBlank() {
        assertFalse(ClientConfig.host().isBlank());
    }

    @Test
    void portIsValid() {
        int port = ClientConfig.port();
        assertTrue(port > 0 && port <= 65535);
    }

    @Test
    void defaultAddressMatchesHostAndPort() {
        InetSocketAddress address = ClientConfig.defaultAddress();
        assertEquals(ClientConfig.host(), address.getHostString());
        assertEquals(ClientConfig.port(), address.getPort());
    }
}
