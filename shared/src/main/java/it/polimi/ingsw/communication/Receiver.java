package it.polimi.ingsw.communication;

import it.polimi.ingsw.communication.message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Receives messages from a socket.
 */
public abstract class Receiver implements Runnable {
    protected final Socket socket;
    protected final BufferedReader in;

    public Receiver(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            String read;
            try {
                read = in.readLine();
            } catch (IOException e) {
                break;
            }
            if (read == null) // the other end closed the connection
                break;
            Message message = GsonProvider.get().fromJson(read, Message.class);
            if (message != null)
                messageSwitch(message);
        }
        onDisconnect();
    }

    protected abstract void messageSwitch(Message message);

    /**
     * Called once when the connection is lost. By default closes the socket.
     */
    protected void onDisconnect() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

}
