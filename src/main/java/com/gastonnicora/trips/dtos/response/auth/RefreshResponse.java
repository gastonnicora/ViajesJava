package com.gastonnicora.trips.dtos.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para representar la respuesta de la renovación de tokens.
 *
 * <p>
 * Contiene un nuevo token JWT de acceso y, cuando corresponde, un nuevo refresh
 * token utilizado para futuras renovaciones.
 * </p>
 *
 * <p>
 * En clientes web, el nuevo refresh token puede gestionarse mediante una cookie
 * HTTP y, por este motivo, el campo {@code refreshToken} puede ser {@code null}
 * en la respuesta.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(description = "Respuesta generada tras renovar correctamente los tokens de autenticación")
@NoArgsConstructor
public class RefreshResponse {

    /**
     * Nuevo token JWT utilizado para autenticar las solicitudes del usuario.
     */
    @Schema(
            description = "Nuevo token JWT de acceso.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0Ijox.NTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    )
    private String token;

    /**
     * Nuevo token utilizado para renovar el token JWT de acceso.
     *
     * <p>
     * Este campo puede ser {@code null} para clientes web, ya que el refresh
     * token se almacena en una cookie HTTP.
     * </p>
     */
    @Schema(
            description = "Nuevo token de refresco UUID. Puede ser nulo para clientes web.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            nullable = true
    )
    private String refreshToken;

    /**
     * Crea una respuesta de renovación de tokens con los valores
     * proporcionados.
     *
     * @param token nuevo token JWT de acceso
     * @param refreshToken nuevo token UUID utilizado para renovar el token de
     * acceso; puede ser {@code null} para clientes web
     */
    public RefreshResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    /**
     * Obtiene el nuevo token JWT de acceso.
     *
     * @return token JWT de acceso
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el nuevo token JWT de acceso.
     *
     * @param token nuevo token JWT de acceso
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene el nuevo token de refresco UUID.
     *
     * @return token de refresco UUID, o {@code null} para clientes web
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Establece el nuevo token de refresco UUID.
     *
     * @param refreshToken nuevo token de refresco UUID
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
