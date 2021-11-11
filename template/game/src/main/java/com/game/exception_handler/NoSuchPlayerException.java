package com.game.exception_handler;

public class NoSuchPlayerException extends RuntimeException{

    public NoSuchPlayerException(String message) {
        super(message);
    }

    public NoSuchPlayerException() {
        super();
    }
}
