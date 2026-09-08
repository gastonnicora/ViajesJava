package com.gastonnicora.trips.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio de gestión de empresas.
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * empresas, como la creación, actualización, eliminación, y obtención de
 * empresas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final GeocodingService geocodingService;

    /**
     * Crea el servicio encargado de gestionar empresas.
     *
     * @param companyRepository repositorio utilizado para persistir y consultar
     * empresas
     * @param companyMapper mapper utilizado para convertir empresas a DTOs
     * @param geocodingService servicio utilizado para obtener direcciones a
     * partir de coordenadas geográficas
     */
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper,
            GeocodingService geocodingService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.geocodingService = geocodingService;
    }

    /**
     * Crea una nueva empresa a partir de los datos proporcionados.
     *
     * <p>
     * Antes de persistir la empresa, obtiene su dirección utilizando las
     * coordenadas geográficas proporcionadas mediante el servicio de
     * geocodificación.
     * </p>
     *
     * @param companyCreate datos necesarios para crear la empresa
     * @return DTO correspondiente a la empresa creada
     * @throws BadRequestException si no se puede obtener una dirección válida
     * @see GeocodingService#obtenerDireccion(double, double)
     * @see CompanyRepository#save(Company)
     * @see CompanyMapper#toDTO(Company)
     */
    public CompanyDTO createCompany(CompanyCreate companyCreate) {
        AddressResponse addressR = geocodingService.obtenerDireccion(companyCreate.getLatitude(),
                companyCreate.getLongitude());
        if (addressR.displayName() == null) {
            throw new BadRequestException("Dirección inválida");
        }
        String address = addressR.displayName();

        Company company = new Company(companyCreate.getName(), address, companyCreate.getLatitude(),
                companyCreate.getLongitude(),
                companyCreate.getEmail(), companyCreate.getPhone());
        company = companyRepository.save(company);
        return companyMapper.toDTO(company);
    }

    /**
     * Obtiene una empresa mediante su UUID.
     *
     * @param uuid UUID de la empresa
     * @return DTO de la empresa encontrada
     * @throws NotFoundException si no existe una empresa con el UUID indicado
     * @see CompanyRepository#findByUuid(UUID)
     * @see CompanyMapper#toDTO(Company)
     */
    public CompanyDTO getCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        return companyMapper.toDTO(company);
    }

    /**
     * Obtiene la entidad de una empresa mediante su UUID.
     *
     * <p>
     * Este método se utiliza internamente por otros servicios que necesitan
     * trabajar directamente con la entidad {@link Company}.
     * </p>
     *
     * @param uuid UUID de la empresa
     * @return entidad de la empresa encontrada
     * @throws NotFoundException si no existe una empresa con el UUID indicado
     */
    public Company getCompanyEntity(UUID uuid) {
        return companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
    }

    /**
     * Convierte una lista de entidades de empresa en una respuesta
     * paginada/simple de tipo {@link ListResponse}.
     *
     * @param companies empresas a convertir
     * @return respuesta que contiene los DTOs de las empresas
     */
    public ListResponse<CompanyDTO> toListResponse(List<Company> companies) {
        return new ListResponse<>(companyMapper.toDTOList(companies));
    }

    /**
     * Actualiza los datos de una empresa.
     *
     * <p>
     * Obtiene nuevamente la dirección a partir de las coordenadas
     * proporcionadas y persiste los cambios realizados sobre la empresa.
     * </p>
     *
     * @param uuid UUID de la empresa a actualizar
     * @param companyCreate nuevos datos de la empresa
     * @return DTO de la empresa actualizada
     * @throws NotFoundException si la empresa no existe
     * @throws BadRequestException si no se puede obtener una dirección válida
     */
    @Transactional
    public CompanyDTO updateCompany(UUID uuid, CompanyCreate companyCreate) {
        Company company = this.getCompanyEntity(uuid);
        AddressResponse addressR = geocodingService.obtenerDireccion(companyCreate.getLatitude(),
                companyCreate.getLongitude());
        if (addressR.displayName() == null) {
            throw new BadRequestException("Dirección inválida");
        }
        String address = addressR.displayName();
        company.setName(companyCreate.getName());
        company.setEmail(companyCreate.getEmail());
        company.setPhone(companyCreate.getPhone());
        company.setLatitude(companyCreate.getLatitude());
        company.setLongitude(companyCreate.getLongitude());
        company.setAddress(address);
        return companyMapper.toDTO(companyRepository.save(company));
    }

    /**
     * Desactiva una empresa mediante su UUID.
     *
     * <p>
     * La empresa no se elimina físicamente de la base de datos; se marca como
     * inactiva.
     * </p>
     *
     * @param uuid UUID de la empresa a desactivar
     * @throws NotFoundException si la empresa no existe
     */
    @Transactional
    public void deleteCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        company.setActive(false);
        companyRepository.save(company);
    }

}
