package dihoon.bulletinboardback.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
