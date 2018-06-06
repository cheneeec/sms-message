package com.gongsj.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnSupportPlatformException extends RuntimeException {

    public UnSupportPlatformException(String message) {
        super(message);
    }
}
