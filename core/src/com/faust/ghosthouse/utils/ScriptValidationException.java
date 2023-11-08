package com.faust.ghosthouse.utils;

/**
 * @author Jacopo "Faust" Buttiglieri
 *
 * Custom exception for script validation
 */
public class ScriptValidationException extends Exception{
    public ScriptValidationException(String message) {
        super(message);
    }
}
