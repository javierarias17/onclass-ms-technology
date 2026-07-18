package co.com.pragma.model.technology.exceptions;

import co.com.pragma.model.exceptions.FunctionalException;

import java.util.Map;

public class TechnologyAlreadyExistsException extends FunctionalException {
    public TechnologyAlreadyExistsException(String message, Map<String, String> errors) {
        super(message, errors);
    }
}