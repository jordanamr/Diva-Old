package fr.aquazus.diva.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class DivaConfiguration {

    private String fileName;
    public static boolean debug;

    public DivaConfiguration(String fileName) {
        this.fileName = fileName;
    }

    protected void read() throws IOException, NumberFormatException {
        throw new UnsupportedOperationException();
    }

    protected String getFileName() {
        return this.fileName;
    }
}
