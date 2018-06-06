package com.gongsj.core.exception;

/**
 * 当平台欠费时，产生该异常
 */
public class PlatArrearsException extends Exception {
    public PlatArrearsException(String message) {
        super(message);
    }

    public PlatArrearsException() {
        super();
    }

    public PlatArrearsException(String message, Throwable cause) {
        super(message, cause);
    }
}
