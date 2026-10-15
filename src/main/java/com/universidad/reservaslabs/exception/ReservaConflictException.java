package com.universidad.reservaslabs.exception;

/** Conflicto de negocio: el estado actual impide la operación (HTTP 409). */
public class ReservaConflictException extends RuntimeException {
    public ReservaConflictException(String mensaje) { super(mensaje); }
}
