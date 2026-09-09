package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles disponibles para los usuarios de la
 * plataforma.
 *
 * <p>
 * Cada rol identifica un nivel de responsabilidad dentro del sistema y puede
 * utilizarse para determinar el acceso del usuario a las distintas
 * funcionalidades de la aplicación.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
public enum Role {

    /**
     * Rol correspondiente a un usuario estándar de la plataforma.
     */
    USER,
    /**
     * Rol correspondiente a un administrador con permisos para gestionar
     * recursos y usuarios básicos del sistema.
     */
    ADMIN,
    /**
     * Rol correspondiente al super administrador, con acceso completo a las
     * funcionalidades del sistema.
     */
    SUPER_ADMIN,
    /**
     * Rol correspondiente al administrador de la plataforma, encargado de la
     * gestión de empresas, usuarios, viajes y reportes generales.
     */
    PLATFORM_ADMIN,
    /**
     * Rol correspondiente al agente de soporte, encargado de la gestión de
     * tickets de soporte y de los reportes relacionados.
     */
    SUPPORT_AGENT,
    /**
     * Rol correspondiente al analista, encargado de generar y analizar
     * información relacionada con ventas, usuarios y viajes.
     */
    ANALYST,
    /**
     * Rol correspondiente al responsable financiero, encargado de la gestión de
     * facturación y de los reportes financieros.
     */
    FINANCE_MANAGER,
    /**
     * Rol correspondiente al responsable de recursos humanos, encargado de la
     * gestión de empleados y de los reportes de personal.
     */
    HR_MANAGER

}
