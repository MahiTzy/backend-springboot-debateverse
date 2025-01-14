package com.server.debateverse.Exceptions;

public class BlockedUserException extends RuntimeException {
    public BlockedUserException(String message) {
        super(message);
    }
    
}
