package com.gastonnicora.trips.controllers.api;

import java.util.List;
import java.util.Set;
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

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.request.company.WorkerCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;
import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de empresas.
 * <p>
 * Este controlador maneja todas las operaciones relacionadas con las empresas,
 * como la creación, modificación, eliminación y obtención de empresas a través
 * de los endpoints definidos en la URL "/api/companies".
 * </p>
 *
 * <p>
 * Este controlador utiliza el servicio {@link CompanyService} para realizar las
 * operaciones de negocio relacionadas con la gestión de empresas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company", description = "Gestión de empresas")
public class CompanyController {

    private final CompanyService companyService;
    private final WorkerService workerService;
    private final UserService userService;

    /**
     * Crea una instancia del controlador de empresas.
     *
     * @param companyService servicio encargado de la gestión de empresas
     * @param workerService servicio encargado de la gestión de trabajadores
     * @param userService servicio encargado de la gestión de usuarios
     */
    public CompanyController(CompanyService companyService, WorkerService workerService, UserService userService) {
        this.companyService = companyService;
        this.workerService = workerService;
        this.userService = userService;
    }

    /**
     * Crea una nueva empresa y registra al usuario autenticado como
     * propietario.
     *
     * <p>
     * La dirección se obtiene a partir de las coordenadas proporcionadas y se
     * valida antes de persistir la empresa.
     * </p>
     *
     * @param companyCreate datos de la empresa a crear
     * @return datos de la empresa creada
     * @throws ValidationException si los datos proporcionados no son válidos
     * @throws BadRequestException si las coordenadas no permiten obtener una
     * dirección válida
     * @see CompanyService#createCompany(CompanyCreate)
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Crear empresa",
            description = "Crea una nueva empresa para el usuario autenticado y "
            + "lo registra como propietario de la empresa."
    )
    public CompanyDTO createCompany(@Valid @RequestBody CompanyCreate companyCreate) {
        User currentUser = userService.getUser(getCurrentUserUuid());

        CompanyDTO companyDTO = companyService.createCompany(companyCreate);
        Company company = companyService.getCompanyEntity(companyDTO.getUuid());

        workerService.createWorkerOwner(currentUser, company);
        return companyDTO;
    }

    /**
     * Obtiene los detalles de una empresa por su UUID.
     * <p>
     * Este endpoint obtiene los detalles de una empresa por su UUID.
     * </p>
     *
     * @param uuid UUID de la empresa que se quiere obtener.
     * @return CompanyDTO con los detalles de la empresa.
     * @see CompanyService#getCompany(UUID)
     */
    @GetMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Obtener empresa",
            description = "Obtiene una empresa utilizando su UUID."
    )
    public CompanyDTO getCompany(@PathVariable UUID uuid) {
        return companyService.getCompany(uuid);
    }

    /**
     * Obtiene todas las empresas del usuario.
     * <p>
     * Este endpoint obtiene todas las empresas del usuario.
     * </p>
     *
     * @param uuid UUID del usuario.
     * @return Lista de empresas del usuario.
     * @see CompanyService#getCompaniesByUser(UUID)
     */
    @GetMapping("/owner/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(
            summary = "Obtener empresas de un usuario",
            description = "Obtiene las empresas asociadas a un usuario mediante su UUID. "
            + "Requiere rol ADMIN o SUPER_ADMIN."
    )
    public ListResponse<CompanyDTO> getCompaniesByUser(@PathVariable UUID uuid) {
        userService.getUser(uuid);
        List<Company> companies = workerService.getCompaniesByOwner(uuid);
        return companyService.toListResponse(companies);
    }

    /**
     * Obtiene todas las empresas del usuario actual.
     * <p>
     * Este endpoint obtiene todas las empresas del usuario actual.
     * </p>
     *
     * @return Lista de empresas del usuario actual.
     * @see CompanyService#getCompaniesByCurrentUser()
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Obtener mis empresas",
            description = "Obtiene las empresas asociadas al usuario autenticado."
    )
    public ListResponse<CompanyDTO> getCompaniesByCurrentUser() {
        UUID uuid = getCurrentUserUuid();
        userService.getUser(uuid);
        List<Company> companies = workerService.getCompaniesByOwner(uuid);
        return companyService.toListResponse(companies);
    }

    /**
     * Actualiza los detalles de una empresa por su UUID.
     * <p>
     * Este endpoint actualiza los detalles de una empresa por su UUID.
     * </p>
     * <p>
     * Valida los datos antes de guardar los cambios.
     * </p>
     *
     * @param uuid UUID de la empresa que se quiere actualizar.
     * @param company {@link CompanyCreate} con los nuevos datos de la empresa.
     * @return CompanyDTO con los detalles de la empresa actualizada.
     * @see CompanyService#updateCompany(UUID, CompanyCreate)
     */
    @PutMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("""
                @companySecurity.hasAnyRole(
                    #uuid,
                    T(com.gastonnicora.trips.enums.RoleCompany).OWNER,
                    T(com.gastonnicora.trips.enums.RoleCompany).ADMIN
                )
            """)
    @Operation(summary = "Modificar empresa", description = "Modifica una empresa por su uuid")
    public CompanyDTO updateCompany(@PathVariable("uuid") UUID uuid, @Valid @RequestBody CompanyCreate company) {
        return companyService.updateCompany(uuid, company);
    }

    /**
     * Elimina una empresa por su UUID.
     * <p>
     * Este endpoint elimina una empresa por su UUID.
     * </p>
     *
     * @param uuid UUID de la empresa que se quiere eliminar.
     * @see CompanyService#deleteCompany(UUID)
     */
    @DeleteMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasRole(#uuid, T(com.gastonnicora.trips.enums.RoleCompany).OWNER)")
    @Operation(summary = "Eliminar empresa", description = "Elimina una empresa por su uuid")
    public void deleteCompany(@PathVariable("uuid") UUID uuid) {
        companyService.deleteCompany(uuid);
    }

//TODO: Agregar endpoint para obtener todas las empresas
}
