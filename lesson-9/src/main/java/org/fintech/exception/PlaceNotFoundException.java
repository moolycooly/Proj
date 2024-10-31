package org.fintech.exception;

public class PlaceNotFoundException extends RuntimeException{
    int id;
    public PlaceNotFoundException(int id) {
        super("Place with id " + id + " not found");
        this.id = id;
    }

}
