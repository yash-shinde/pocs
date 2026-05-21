package com.learning.resilientorders.exception;

public class TemporaryDependencyException extends RuntimeException {
    public TemporaryDependencyException(String message) {
        super(message);
    }
}
