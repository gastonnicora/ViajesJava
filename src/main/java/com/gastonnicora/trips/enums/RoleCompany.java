package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles disponibles dentro de una empresa.
 *
 * <p>
 * Cada rol representa un conjunto de responsabilidades y permisos dentro de la
 * empresa. Los roles pueden asignarse a los trabajadores para determinar el
 * acceso a las distintas funcionalidades disponibles.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
public enum RoleCompany {

    /**
     * Rol correspondiente al propietario de la empresa, con todos los permisos
     * sobre la misma.
     */
    OWNER,

    /**
     * Rol correspondiente al vendedor, encargado de gestionar viajes y
     * reservas, además de generar reportes de ventas.
     *
     * <p>
     * Puede crear, modificar y eliminar viajes, así como consultar y cancelar
     * reservas.
     * </p>
     */
    SELLER,

    /**
     * Rol correspondiente al conductor, encargado de gestionar los viajes que
     * tiene asignados y actualizar su estado.
     *
     * <p>
     * Puede actualizar los viajes a los estados correspondientes a su
     * ejecución, como en curso o completado, y gestionar su perfil.
     * </p>
     */
    DRIVER,

    /**
     * Rol correspondiente al administrador de la empresa, encargado de
     * gestionar usuarios, viajes y reportes dentro de su empresa.
     *
     * <p>
     * Puede generar reportes relacionados con las ventas y los usuarios de la
     * empresa.
     * </p>
     */
    ADMIN,

    /**
     * Rol correspondiente al responsable de recursos humanos, encargado de la
     * gestión de empleados y de los reportes de personal dentro de la empresa.
     */
    HR_MANAGER
}
