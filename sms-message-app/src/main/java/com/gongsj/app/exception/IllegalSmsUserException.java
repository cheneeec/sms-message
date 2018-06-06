package com.gongsj.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalSmsUserException extends RuntimeException {

    public IllegalSmsUserException(String message) {
        super(message);
    }

    public IllegalSmsUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalSmsUserException(Throwable cause) {
        super(cause);
    }
}
