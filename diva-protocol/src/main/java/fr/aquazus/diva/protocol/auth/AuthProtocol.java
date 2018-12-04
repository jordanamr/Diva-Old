package fr.aquazus.diva.protocol.auth;

import fr.aquazus.diva.protocol.Protocol;

public class AuthProtocol implements Protocol {

    private static AuthProtocol instance = null;

    public static AuthProtocol getInstance() {
        if (instance == null) instance = new AuthProtocol();
        return instance;
    }

    @Override
    public boolean handle(String packet) {
        if (packet.length() < 2) return false;
        switch (packet.charAt(0)) {
            case 'A':
                switch (packet.charAt(1)) {

                }
        }
        return false;
    }
}

