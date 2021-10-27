package com.game.exception_handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<HttpStatus> handleException(IncorrectPlayerArguments exception){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<HttpStatus> handleException(NoSuchPlayerException exception){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
