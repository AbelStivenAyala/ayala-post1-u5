package com.universidad.reservaslabs.exception;

/** Datos de la reserva que violan una regla propia del objeto: horario o duración (HTTP 400). */
public class ReservaInvalidaException extends RuntimeException {
    public ReservaInvalidaException(String mensaje) { super(mensaje); }
}
