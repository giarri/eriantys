package it.polimi.ingsw.startUp;

import it.polimi.ingsw.client.userInterface.cli.Cli;
import it.polimi.ingsw.server.communication.ServerMain;
import it.polimi.ingsw.client.userInterface.gui.Gui;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String PORT_ARG = "--port";
    private static final String PORT_ENV = "ERIANTYS_PORT";

    public static void main(String[] args) {
        System.out.println(Outputs.CLEAR_SCREEN);
        System.out.println(Outputs.START);
        int ScreenSize = 0; // = System.getProperty("screenSize");
        Outputs.getTitle(ScreenSize);
        switch (getUserType(withoutPortArgs(args))) {
            case SERVER -> {
                int port = getPort(args);
                try {
                    new ServerMain(port).run();
                } catch (IllegalStateException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
            case CLI -> new Cli();
            case GUI -> Application.launch(Gui.class);
        }
    }

    /**
     * Server port: "--port <n>" argument, else the ERIANTYS_PORT env var, else ServerMain.DEFAULT_PORT.
     */
    private static int getPort(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (PORT_ARG.equals(args[i])) {
                if (i + 1 >= args.length)
                    exitWithUsage("missing value after " + PORT_ARG);
                return parsePort(args[i + 1], PORT_ARG);
            }
        }
        String env = System.getenv(PORT_ENV);
        if (env != null && !env.isBlank())
            return parsePort(env, PORT_ENV);
        return ServerMain.DEFAULT_PORT;
    }

    private static int parsePort(String value, String source) {
        try {
            int port = Integer.parseInt(value.strip());
            if (port >= 1 && port <= 65535)
                return port;
        } catch (NumberFormatException ignored) {}
        exitWithUsage("invalid port '" + value + "' from " + source + " (expected 1-65535)");
        return -1; // unreachable
    }

    private static void exitWithUsage(String error) {
        System.err.println("Error: " + error);
        System.err.println("Usage: server [" + PORT_ARG + " <port>]  (or set " + PORT_ENV + "; default " + ServerMain.DEFAULT_PORT + ")");
        System.exit(1);
    }

    private static String[] withoutPortArgs(String[] args) {
        List<String> filtered = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (PORT_ARG.equals(args[i]))
                i++; // skip the value as well
            else
                filtered.add(args[i]);
        }
        return filtered.toArray(new String[0]);
    }

    private static UserType getUserType(String[] args) {
        if (args.length == 0 || args[0] == null)
            return getUserType();
        for (String argIn : args) {
            if(UserType.getUserType(argIn) != null) {
                return UserType.getUserType(argIn);
            }
        }
        System.err.println(Outputs.USER_TYPE_INVALID);
        return getUserType();
    }

    private static UserType getUserType() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(Outputs.USER_TYPE_REQUEST);
        UserType userType;
        try {
            userType = UserType.getUserType(br.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(userType == null) {
            System.out.println(Outputs.USER_TYPE_INVALID);
            userType = getUserType();
        }
        return userType;
    }
}
