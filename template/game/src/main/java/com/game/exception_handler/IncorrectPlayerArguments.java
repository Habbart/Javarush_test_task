package com.game.exception_handler;



public class IncorrectPlayerArguments extends RuntimeException{

    public IncorrectPlayerArguments(String message) {
        super(message);
    }

    public IncorrectPlayerArguments() {
        super();
    }
}
