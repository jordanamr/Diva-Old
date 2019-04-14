package fr.aquazus.diva.game.protocol.server;

import fr.aquazus.diva.common.protocol.ProtocolMessage;
import fr.aquazus.diva.game.network.player.Character;

import java.time.LocalDate;

public class DateTimeMessage extends ProtocolMessage {

    private int day;
    private int month;
    private int year;

    public DateTimeMessage() {
        LocalDate localDate = LocalDate.now();
        this.day = localDate.getDayOfMonth();
        this.month = localDate.getMonthValue();
        this.year = localDate.getYear();
    }

    @Override
    public String serialize() {
        return "BD" + (year - 1370) + "|" + (month - 1) + "|" + day;
    }
}
