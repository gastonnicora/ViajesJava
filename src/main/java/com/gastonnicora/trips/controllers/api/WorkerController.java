package com.gastonnicora.trips.controllers.api;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.request.company.WorkerCreate;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de vehículos.
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-07
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Worker", description = "Gestión de trabajadores")
public class WorkerController {

    private final WorkerService workerService;
    private final CompanyService companyService;
    private final UserService userService;

    public WorkerController(WorkerService workerService, CompanyService companyService, UserService userService) {
        this.workerService = workerService;
        this.companyService = companyService;
        this.userService = userService;
    }

    /**
     * Obtiene los trabajadores de una empresa.
     * <p>
     * Este endpoint devuelve los trabajadores asociados a una empresa
     * identificada por su UUID.
     * </p>
     *
     * @param uuid UUID de la empresa.
     * @return {@link WorkersByCompany} con los trabajadores de la empresa.
     * @see CompanyService#getWorkersByCompany(UUID)
     */
    @GetMapping("/companies/{uuid}/workers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#uuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Obtener trabajadores de empresa", description = "Obtiene los trabajadores de una empresa por su uuid")
    public WorkersByCompany getWorkersByCompany(@PathVariable("uuid") UUID uuid) {
        return workerService.getWorkersByCompany(uuid);
    }

    // TODO 🚀: Falta relacion entre trabajador y empresa
    /**
     * Agrega un trabajador a una empresa.
     * <p>
     * Este endpoint agrega un trabajador a una empresa por su UUID.
     * </p>
     *
     * @param uuid UUID de la empresa a la que se quiere agregar el trabajador.
     * @param workerCreate {@link WorkerCreate} con los datos del trabajador a
     * agregar.
     * @return {@link WorkerDTO} con los datos del trabajador agregado.
     * @see CompanyService#createWorker(UUID, UUID, Set<RoleCompany>)
     */
    @PostMapping("/companies/{uuid}/worker")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#uuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Agregar worker a empresa", description = "Agrega un worker a una empresa por su UUID")
    public WorkerDTO createWorker(@PathVariable("uuid") UUID uuid, @RequestBody @Valid WorkerCreate workerCreate) {
        Company company = companyService.getCompanyEntity(uuid);
        User user = userService.getUser(workerCreate.getUserUuid());
        return workerService.createWorker(user, company, workerCreate.getRoles());
    }

    /**
     * Elimina un trabajador de una empresa.
     * <p>
     * Este endpoint elimina un trabajador de una empresa por su UUID.
     * </p>
     *
     * @param companyUuid UUID de la empresa.
     * @param userUuid UUID del trabajador.
     * @see CompanyService#deleteWorker(UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/worker/{userUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Eliminar trabajador de empresa", description = "Elimina un trabajador de una empresa por su UUID")
    public void deleteWorker(@PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("userUuid") UUID userUuid) {
        companyService.getCompanyEntity(companyUuid);
        userService.getUser(userUuid);
        workerService.deleteWorker(userUuid, companyUuid);
    }

    /**
     * Actualiza los roles de un trabajador en una empresa.
     * <p>
     * Este endpoint actualiza los roles de un trabajador en una empresa por su
     * UUID.
     * </p>
     *
     * @param companyUuid UUID de la empresa.
     * @param userUuid UUID del trabajador.
     * @param workerCreate {@link WorkerCreate} con los nuevos roles del
     * trabajador.
     * @return {@link WorkerDTO} con los datos del trabajador actualizado.
     * @see CompanyService#updateWorker(UUID, UUID, Set<RoleCompany>)
     */
    @PutMapping("/companies/{companyUuid}/worker/{userUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Actualizar roles de trabajador en empresa", description = "Actualiza los roles de un trabajador en una empresa por su UUID")
    public WorkerDTO updateWorkerRoles(@PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("userUuid") UUID userUuid,
            @RequestBody @Valid WorkerCreate workerCreate) {

        companyService.getCompanyEntity(companyUuid);
        userService.getUser(userUuid);
        return workerService.updateWorker(userUuid, companyUuid, workerCreate.getRoles());
    }

    /**
     * Obtiene los trabajos asociados al usuario actual.
     * <p>
     * <strong>Requiere autenticación </strong>
     * </p>
     * <p>
     * Este endpoint obtiene los trabajos asociados al usuario actual. Se
     * realiza la validación de los datos antes de obtener los trabajos.
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link UserService} para obtener los
     * trabajos del usuario actual.
     * </p>
     *
     * @return {@link WorkersByUser} con los trabajos del usuario actual.
     * @see UserService#getWorkersByCurrentUser()
     */
    @GetMapping("/users/workers")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener trabajos del usuario actual", description = "Obtiene los trabajos asociados al usuario actual")
    public WorkersByUser getWorkersByCurrentUser() {
        return workerService.getWorkersByUser(getCurrentUserUuid());
    }
}
