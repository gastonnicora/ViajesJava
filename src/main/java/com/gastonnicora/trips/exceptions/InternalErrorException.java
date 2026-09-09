package com.gastonnicora.trips.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada de la aplicación para representar errores internos
 * del servidor (HTTP 500 - Internal Server Error).
 * <p>
 * Se utiliza cuando ocurre un error durante el procesamiento de una operación
 * que impide completar la solicitud correctamente.
 * </p>
 *
 * <p>
 * Se utiliza en conjunto con
 * {@link com.gastonnicora.trips.exceptions.handler.GlobalExceptionHandler}
 * para generar respuestas de error estandarizadas con código HTTP 500.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-22
 */
public class InternalErrorException extends RuntimeException {

    /**
     * Código HTTP asociado a la excepción (500 - Internal Server Error).
     */
    private final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    /**
     * Explicación adicional asociada al error.
     */
    private final String explain;

    /**
     * Constructor que inicializa la excepción con un mensaje y una explicación.
     *
     * @param message Mensaje descriptivo del error.
     * @param explain Explicación adicional asociada al error.
     */
    public InternalErrorException(String message, String explain) {
        super(message);
        this.explain = explain;
    }

    /**
     * Obtiene el código HTTP asociado a la excepción.
     *
     * @return Código HTTP 500.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Obtiene la explicación adicional asociada al error.
     *
     * @return Explicación del error.
     */
    public String getExplain() {
        return explain;
    }

}
