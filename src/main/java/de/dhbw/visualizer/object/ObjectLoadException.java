package de.dhbw.visualizer.object;

public class ObjectLoadException extends RuntimeException {

    public ObjectLoadException(Exception ex) {
        super(ex);
    }

    public ObjectLoadException(String msg) {
        super(msg);
    }
}
