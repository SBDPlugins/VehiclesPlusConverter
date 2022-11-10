package tech.sbdevelopment.vehiclesplusconverter.api;

import java.io.IOException;

public class ConversionException extends IOException {
    public ConversionException(String before, String filename) {
        super(before + " " + filename + ".yml");
    }
}
