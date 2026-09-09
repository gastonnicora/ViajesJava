package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar recursos no
 * encontrados (HTTP 404 - Not Found).
 * <p>
 * Se utiliza cuando un cliente solicita un recurso que no existe o no se
 * encuentra disponible.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 404.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
public class NotFoundException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (404 - Not Found).
     */
    private final int status = HttpStatus.NOT_FOUND.value();

    /**
     * Constructor que inicializa la excepción con un mensaje descriptivo.
     *
     * @param message Mensaje descriptivo del error.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 404.
     */
    public int getStatus() {
        return status;
    }
}
