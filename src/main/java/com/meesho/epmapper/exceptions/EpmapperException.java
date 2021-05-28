package com.meesho.epmapper.exceptions;

public class EpmapperException extends RuntimeException {
    public EpmapperException(String message) {
        super(message);
    }

    public EpmapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
