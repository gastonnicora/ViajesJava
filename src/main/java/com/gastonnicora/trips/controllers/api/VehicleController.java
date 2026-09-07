package com.gastonnicora.trips.controllers.api;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de vehículos.
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Vehicle", description = "Gestión de vehículos")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     *
     * Constructor del controlador VehicleController.
     *
     * @param VehicleService Servicio del Transporte
     */
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Obtiene los detalles de un vehículo por su UUID.
     * <p>
     * Este endpoint obtiene los detalles de un vehículo por su UUID.
     * </p>
     *
     * @param uuid UUID del vehículo que se quiere obtener.
     * @return VehicleDTO con los detalles del vehículo.
     * @see VehicleService#getVehicle(UUID)
     */
    @GetMapping("/companies/{companyUuid}/vehicle/{vehicleUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Obtener vehículo", description = "Obtiene los detalles de un vehículo por su uuid")
    public VehicleDTO getVehicle(@PathVariable("companyUuid") UUID companyUuid, @PathVariable("vehicleUuid") UUID uuid) {
        return vehicleService.getVehicle(uuid);
    } 

    /**
     * Agrega un vehículo a una empresa.
     * <p>
     * Este endpoint agrega un vehículo a una empresa por su UUID.
     * </p>
     *
     * @param companyUuid UUID de la empresa.
     * @param vehicleCreate {@link VehicleCreate} con los datos del vehículo a
     * agregar.
     * @return {@link VehicleDTO} con los datos del vehículo agregado.
     * @see CompanyService#createVehicle(UUID, VehicleCreate)
     */
    @PostMapping("/companies/{companyUuid}/vehicle")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Agregar vehículo a empresa", description = "Agrega un vehículo a una empresa por su UUID")
    public VehicleDTO createVehicle(@PathVariable("companyUuid") UUID companyUuid, @RequestBody @Valid VehicleCreate vehicleCreate) {
        return vehicleService.createVehicle(companyUuid, vehicleCreate);
    }

    /**
     * Elimina un vehículo de una empresa.
     * <p>
     * Este endpoint elimina un vehículo de una empresa por su UUID.
     * </p>
     *
     * @param companyUuid UUID de la empresa.
     * @param vehicleUuid UUID del vehículo.
     * @see CompanyService#deleteVehicle(UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/vehicle/{vehicleUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Eliminar un vehículo de una empresa", description = "Elimina un vehículo de una empresa por su UUID")
    public void deleteVehicle(@PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("vehicleUuid") UUID vehicleUuid) {
        vehicleService.deleteVehicle(companyUuid, vehicleUuid);
    }
    
}
