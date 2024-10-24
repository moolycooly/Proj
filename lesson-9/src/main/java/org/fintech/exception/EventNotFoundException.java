package org.fintech.exception;


public class EventNotFoundException extends RuntimeException{
    int id;
    public EventNotFoundException(int id) {
        super("Event with id " + id + " not found");
        this.id = id;
    }

}
