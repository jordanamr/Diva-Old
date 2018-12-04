package fr.aquazus.diva.auth;

public class AuthServer {

    private static AuthServer instance = null;

    public static AuthServer getInstance() {
        if (instance == null) instance = new AuthServer();
        return instance;
    }

    public static void main(String[] args) {
        AuthServer.getInstance().start();
    }

    public void start() {
        System.out.println("Hello World!");
    }
}
