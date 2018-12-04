package fr.aquazus.diva.protocol;

public interface Protocol {
    String version = "1.29.1";
    /**
     * Handle an incoming packet
     * @param packet The packet to handle
     * @return <b>true</b> if the packet exists and was handled, <b>false</b> if not
     */
    boolean handle(String packet);
}
