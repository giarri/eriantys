package it.polimi.ingsw.server.communication;

import it.polimi.ingsw.communication.message.subclasses.Ping;
import org.apache.logging.log4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that implements the heart-beat protocol.
 * if a client doesn't ping back before TIMEOUT
 * it gets removed from his game (the game end) or queue.
 */
public class HeartBeatServer implements Runnable {

    private final static Logger logger = LogManager.getLogger(HeartBeatServer.class);

    private final int TIMEOUT = 5000; // [ms]
    private final Set<Client> clients = ConcurrentHashMap.newKeySet();
    private final Set<Client> heartBeats = ConcurrentHashMap.newKeySet();

    /**
     * Adds a client to the connected clients list and starts the heartbeat on him as well
     */
    public void addClient(Client newClient) {
        clients.add(newClient);
        logger.info("Added client");
    }

    /**Validate the connection with the client for another 5 seconds.
     * @param client the key in the Heartbeat server
     */
    public void validateResponse(Client client) {
        if (heartBeats.remove(client)) {
            logger.debug("Ping received from port: " + client.username() + ". Pinging back...");
        }
        else
            logger.info("client in port: " + client.username() + " shouldn't be connected");
    }

    /**
     * Heart-beat server main thread keeps executing the following methods to keep track of the connected players.
     */
    public void run() {
        while(true ) {
            long previousTime = new Date().getTime();
            sendsPingToAll();
            waits(previousTime);
            // iterate over a copy: removeClient mutates heartBeats
            new HashSet<>(heartBeats).forEach(this::removeClient);
        }
    }

    private void sendsPingToAll() {
        clients.forEach(client -> {
            heartBeats.add(client); // before sending, so a fast reply isn't missed
            client.send(new Ping());
        });
    }

    private void waits(long previousTime) {
        try {
            Thread.sleep(TIMEOUT - new Date().getTime() + previousTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeClient(Client client) {
        client.endConnection();
        this.clients.remove(client);
        this.heartBeats.remove(client);
        logger.info("User on port " + client.username() + " disconnected.");
    }

}
