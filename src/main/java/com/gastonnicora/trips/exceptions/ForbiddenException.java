package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar un acceso
 * prohibido o denegado (HTTP 403 - Forbidden).
 * <p>
 * Se utiliza cuando un usuario autenticado intenta acceder a un recurso o
 * realizar una operación para la cual no dispone de los permisos necesarios.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 403.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (403 - Forbidden).
     */
    private final int status = HttpStatus.FORBIDDEN.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public ForbiddenException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 403.
     */
    public int getStatus() {
        return status;
    }
}
