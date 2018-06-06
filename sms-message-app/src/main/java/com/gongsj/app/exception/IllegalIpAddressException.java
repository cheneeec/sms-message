package com.gongsj.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalIpAddressException extends RuntimeException {
    public IllegalIpAddressException() {
        super();
    }

    public IllegalIpAddressException(String message) {
        super(message);
    }
}
