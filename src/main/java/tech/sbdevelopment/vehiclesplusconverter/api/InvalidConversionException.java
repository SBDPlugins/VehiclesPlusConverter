package tech.sbdevelopment.vehiclesplusconverter.api;

public class InvalidConversionException extends ConversionException {
    public InvalidConversionException(String message, String filename) {
        super("Invalid " + message + " found in file", filename);
    }
}
