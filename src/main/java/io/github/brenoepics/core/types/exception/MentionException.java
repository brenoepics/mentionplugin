package io.github.brenoepics.core.types.exception;

public class MentionException extends Exception {
    public MentionException(String message) {
        super(message);
    }

    public MentionException(String message, Throwable cause) {
        super(message, cause);
    }
}
