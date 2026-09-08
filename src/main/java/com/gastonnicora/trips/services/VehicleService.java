package com.gastonnicora.trips.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio encargado de gestionar los vehículos asociados a empresas.
 *
 * <p>
 * Permite crear, consultar y desactivar vehículos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-07
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    /**
     * Crea una instancia del servicio de vehículos.
     *
     * @param vehicleRepository repositorio de vehículos
     * @param vehicleMapper mapper utilizado para convertir vehículos a DTOs
     */
    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    /**
     * Busca un vehículo activo mediante su UUID.
     *
     * @param vehicleUuid UUID del vehículo
     * @return entidad del vehículo encontrado
     * @throws NotFoundException si el vehículo no existe o está inactivo
     */
    public Vehicle findByUuid(UUID vehicleUuid) {
        Vehicle vehicle = vehicleRepository.findByUuidAndActiveTrue(vehicleUuid).orElseThrow(() -> new NotFoundException("Vehículo no encontrado"));
        return vehicle;
    }

    /**
     * Crea un nuevo vehículo asociado a una empresa.
     *
     * @param company Empresa a la que se asociará el vehículo.
     * @param vehicleCreate DTO que contiene los datos del vehículo a crear.
     * @return DTO del vehículo creado.
     * @throws ConflictException si ya existe un vehículo con la misma patente
     * para la empresa.
     */
    public VehicleDTO createVehicle(Company company, VehicleCreate vehicleCreate) {
        if (vehicleRepository
                .findByCompanyUuidAndPlateAndActiveTrue(company.getUuid(), vehicleCreate.getPlate())
                .isPresent()) {
            throw new ConflictException("Ya existe un vehículo activo con esa patente");
        } // TODO 🚀:  modificar patentes para sacar espacios y letras en mayusculas

        Vehicle vehicle = vehicleRepository.save(new Vehicle(company, vehicleCreate.getPlate(), vehicleCreate.getModel(), vehicleCreate.getCapacity()));
        return vehicleMapper.toDTO(vehicle);
    }

    /**
     * Elimina un vehículo existente marcándolo como inactivo.
     *
     * @param vehicleUuid UUID del vehículo a eliminar.
     * @throws NotFoundException si no existe un vehículo con el UUID
     * proporcionado.
     */
    @Transactional
    public void deleteVehicle(UUID companyUuid, UUID vehicleUuid) {
        Vehicle existingVehicle = findByUuid(vehicleUuid);
        if (!companyUuid.equals(existingVehicle.getCompany().getUuid())) {
            throw new ForbiddenException("Acceso denegado");
        }
        existingVehicle.setActive(false);
        vehicleRepository.save(existingVehicle);
    }

    /**
     * Obtiene un vehículo existente por su UUID.
     *
     * @param vehicleUuid UUID del vehículo a obtener.
     * @return DTO del vehículo encontrado.
     * @throws NotFoundException si no existe un vehículo con el UUID
     * proporcionado.
     */
    public VehicleDTO getVehicle(UUID vehicleUuid) {
        Vehicle vehicle = findByUuid(vehicleUuid);
        return vehicleMapper.toDTO(vehicle);
    }

}
