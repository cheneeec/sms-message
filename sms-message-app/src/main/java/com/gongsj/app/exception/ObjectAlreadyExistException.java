package com.gongsj.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ObjectAlreadyExistException extends RuntimeException {
    public ObjectAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public ObjectAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectAlreadyExistException(String message) {
        super(message);
    }
}
