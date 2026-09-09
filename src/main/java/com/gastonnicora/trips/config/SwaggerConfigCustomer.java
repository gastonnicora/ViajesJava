package com.gastonnicora.trips.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Configuración para personalizar la documentación de OpenAPI generada por
 * SpringDoc.
 *
 * <p>
 * Agrega a las operaciones protegidas mediante {@link PreAuthorize} información
 * sobre los roles requeridos para acceder a ellas y permite ordenar
 * alfabéticamente las etiquetas de la documentación.
 * </p>
 *
 * <p>
 * Las expresiones de autorización soportadas incluyen tanto las funciones
 * estándar {@code hasRole} y {@code hasAnyRole} como las funciones
 * personalizadas {@code @companySecurity.hasRole} y
 * {@code @companySecurity.hasAnyRole}.
 * </p>
 *
 * <p>
 * También reconoce roles definidos mediante referencias SpEL a enumeraciones,
 * por ejemplo:
 * </p>
 *
 * <pre>
 * T(com.gastonnicora.trips.enums.RoleCompany).OWNER
 * T(com.gastonnicora.trips.enums.RoleCompany).ADMIN
 * </pre>
 *
 * <p>
 * Estos roles se muestran de forma simplificada en Swagger, evitando exponer la
 * expresión SpEL completa.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Configuration
public class SwaggerConfigCustomer {

    /**
     * Personaliza las operaciones de OpenAPI agregando información sobre los
     * roles definidos mediante {@link PreAuthorize}.
     *
     * <p>
     * Cuando el método asociado a una operación contiene la anotación
     * {@link PreAuthorize}, se analiza su expresión de autorización y se
     * extraen los roles requeridos para agregarlos a la descripción de la
     * operación.
     * </p>
     *
     * <p>
     * Por ejemplo, una expresión como:
     * </p>
     *
     * <pre>
     * @companySecurity.hasAnyRole(
     *     #uuid,
     *     T(com.gastonnicora.trips.enums.RoleCompany).OWNER,
     *     T(com.gastonnicora.trips.enums.RoleCompany).ADMIN
     * )
     * </pre>
     *
     * <p>
     * se mostrará en Swagger como:
     * </p>
     *
     * <pre>
     * 🔒 Requiere rol: OWNER, ADMIN
     * </pre>
     *
     * @return {@link OperationCustomizer} encargado de personalizar las
     * operaciones de OpenAPI
     */
    @Bean
    public OperationCustomizer customizePreAuthorize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            PreAuthorize preAuth = handlerMethod.getMethodAnnotation(PreAuthorize.class);

            if (preAuth != null) {
                String expression = preAuth.value();

                String roles = extractRoles(expression);

                String securityInfo = "🔒 **Requiere rol:** " + roles;

                String existingDescription = operation.getDescription();

                operation.setDescription(
                        (existingDescription == null || existingDescription.isBlank()
                        ? ""
                        : existingDescription + "\n\n")
                        + securityInfo);
            }

            return operation;
        };
    }

    /**
     * Ordena alfabéticamente las etiquetas de la documentación de OpenAPI.
     *
     * <p>
     * Si la definición de OpenAPI no contiene etiquetas, no se realiza ninguna
     * modificación.
     * </p>
     *
     * @return {@link OpenApiCustomizer} encargado de ordenar las etiquetas
     */
    @Bean
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {

            if (openApi.getTags() == null) {
                return;
            }

            openApi.setTags(
                    openApi.getTags().stream()
                            .sorted(Comparator.comparing(Tag::getName))
                            .toList());
        };
    }

    /**
     * Extrae los roles definidos en una expresión de {@link PreAuthorize}.
     *
     * <p>
     * Reconoce roles definidos mediante:
     * </p>
     *
     * <ul>
     * <li>{@code hasRole('ADMIN')}</li>
     * <li>{@code hasAnyRole('ADMIN', 'SUPER_ADMIN')}</li>
     * <li>{@code @companySecurity.hasRole(#uuid, T(...).OWNER)}</li>
     * <li>{@code @companySecurity.hasAnyRole(#uuid, T(...).OWNER, T(...).ADMIN)}</li>
     * </ul>
     *
     * <p>
     * Para las referencias SpEL a enumeraciones, únicamente se extrae el nombre
     * de la constante. Por ejemplo:
     * </p>
     *
     * <pre>
     * T(com.gastonnicora.trips.enums.RoleCompany).OWNER
     * </pre>
     *
     * <p>
     * se transforma en:
     * </p>
     *
     * <pre>
     * OWNER
     * </pre>
     *
     * <p>
     * Si no se encuentra ningún rol reconocido, se devuelve la expresión
     * original como mecanismo de fallback.
     * </p>
     *
     * @param expression expresión de {@link PreAuthorize} que contiene las
     * reglas de autorización
     * @return roles extraídos de la expresión, separados por comas, o la
     * expresión original si no se encuentra ningún rol
     */
    private String extractRoles(String expression) {
        List<String> roles = new ArrayList<>();

        /*
         * Detecta:
         *
         * hasRole('ADMIN')
         * hasAnyRole('ADMIN', 'SUPER_ADMIN')
         *
         * También funciona con espacios:
         *
         * hasAnyRole( 'ADMIN', 'SUPER_ADMIN' )
         */
        Pattern stringRolePattern = Pattern.compile(
                "(?:hasRole|hasAnyRole)\\s*\\(([^)]*)\\)");

        Matcher stringMatcher = stringRolePattern.matcher(expression);

        while (stringMatcher.find()) {
            String[] parts = stringMatcher.group(1).split(",");

            for (String part : parts) {
                String role = part.replaceAll("[\\'\\s]", "");

                if (!role.isEmpty() && !role.startsWith("#")) {
                    roles.add(role);
                }
            }
        }

        /*
         * Detecta roles definidos mediante referencias SpEL a enums:
         *
         * T(com.gastonnicora.trips.enums.RoleCompany).OWNER
         * T(com.gastonnicora.trips.enums.RoleCompany).ADMIN
         */
        Pattern enumRolePattern = Pattern.compile(
                "T\\([^)]*\\)\\.(\\w+)");

        Matcher enumMatcher = enumRolePattern.matcher(expression);

        while (enumMatcher.find()) {
            roles.add(enumMatcher.group(1));
        }

        /*
         * Elimina roles duplicados manteniendo el orden original.
         */
        roles = roles.stream()
                .distinct()
                .toList();

        /*
         * Fallback:
         *
         * Si la expresión no contiene ningún rol reconocido, se mantiene la
         * expresión original para no ocultar información de autorización.
         */
        if (roles.isEmpty()) {
            return expression;
        }

        return String.join(", ", roles);
    }
}
