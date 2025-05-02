package com.example.votingapp.exception;

public class PollNotFoundException extends RuntimeException {
    public PollNotFoundException(String id) {
        super("Poll not found with id: " + id);
    }
}
