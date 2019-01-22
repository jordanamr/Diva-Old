package fr.aquazus.diva.common;

import lombok.extern.slf4j.Slf4j;
import simplenet.Client;
import simplenet.Server;

@Slf4j
public abstract class DivaServer {

    protected void listen(String ip, int port) {
        log.info("Starting net server...");
        Server netServer = new Server(1024);
        netServer.onConnect(netClient -> {
            String clientIp;
            try {
                clientIp = netClient.getChannel().getRemoteAddress().toString().substring(1).split(":")[0];
            } catch (Exception ex) {
                log.error("An error occurred while parsing an user IP", ex);
                if (netClient.getChannel().isOpen()) netClient.close();
                return;
            }
            log.info("[" + clientIp + "] connected!");
            onClientConnect(netClient, clientIp);
        });
        netServer.bind(ip, port);
    }

    protected void onClientConnect(Client netClient, String clientIp) {
        throw new UnsupportedOperationException();
    }
}
