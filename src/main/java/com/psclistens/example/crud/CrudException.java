package com.psclistens.example.crud;

/**
 * Base exception for this application.
 * 
 * @author LYNCHNF
 */
public class CrudException extends Exception {
    private static final long serialVersionUID = 1L;

    public CrudException(String message) {
        super(message);
    }

    public CrudException(String message, Throwable cause) {
        super(message, cause);
    }
}