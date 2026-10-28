package it.polimi.ingsw.server.communication;

import it.polimi.ingsw.communication.command.Command;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.server.controller.GameInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Client {

    private final static Logger logger = LogManager.getLogger(Client.class);
    private final Socket clientsSocket;
    private final LobbyManager lobbyManager;
    private final PrintWriter out;
    private final AtomicBoolean connectionEnded = new AtomicBoolean(false);
    private GameInterface gameInterface;
    private String username;

    public Client(Socket clientsSocket, HeartBeatServer heartBeatServer, LobbyManager lobbyManager, ExecutorService executor) {
        this.clientsSocket = clientsSocket;
        this.lobbyManager = lobbyManager;
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(clientsSocket.getOutputStream(), true);
        } catch (IOException e) {
            logger.info("client @port " + clientsSocket.getPort() + " failed opening the output stream", e);
        }
        this.out = writer;
        heartBeatServer.addClient(this);
        executor.submit(new ServerReceiver(this, heartBeatServer, lobbyManager));
        logger.debug("New Client created @port: " +clientsSocket.getPort());
    }

    public void setup() {

    }

    public synchronized void send(Message message) {
        logger.info("client " + username + " sending data. (" + message.getType() + ").");
        if (out == null)
            return;
        out.println(message.toJson());
    }

    public void executeCommand(Command command, Client client) {
        gameInterface.executeCommand(command, client);
    }


    /**
     * Removes the client from its lobby or game and closes the socket. Only the first call has effect.
     */
    public void endConnection() {
        if (connectionEnded.getAndSet(true))
            return;
        if (gameInterface == null) {
            this.lobbyManager.remove(this);
        } else {
            gameInterface.endGame(this);
        }
        try {
            clientsSocket.close();
        } catch (IOException e) {
            logger.debug("client " + username + " failed closing the socket", e);
        }
    }

    /**
     * Transfer a player from the game to outside the lobby.
     */
    public void putInLobby() {
        gameInterface = null;
        lobbyManager.addPlayerNoPreferences(this);
    }

    public void setGameInterface(GameInterface gameInterface) {
        this.gameInterface = gameInterface;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket clientsSocket() {
        return clientsSocket;
    }

    public String username() {
        return username;
    }
}
