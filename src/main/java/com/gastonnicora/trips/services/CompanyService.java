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
// TODO 🚀: refactorizar para que solo contenga lo de company

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
     * Constructor que inicializa los servicios necesarios para la gestión de
     * empresas.
     *
     * @param userService Servicio de gestión de usuarios.
     * @param companyRepository Repositorio de empresas utilizado para acceder a
     * la base de datos.
     * @param companyMapper Mapper para convertir entidades {@link Company} a
     * DTOs {@link CompanyDTO}.
     * @param geocodingService Servicio para obtener direcciones a partir de
     * coordenadas.
     * @param workerService Servicio de gestión de trabajadores.
     */
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper,
            GeocodingService geocodingService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.geocodingService = geocodingService;
    }

    /**
     * Crea una nueva empresa.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Obtiene el usuario actual mediante {@link UserService}.</li>
     * <li>Obtiene la dirección a partir de las coordenadas proporcionadas
     * mediante {@link GeocodingService}.</li>
     * <li>Crea una nueva instancia de {@link Company} con los datos
     * proporcionados
     * </li>
     * <li>Crea un nuevo trabajador con el rol de dueño.</li>
     * <li>Guarda la nueva empresa en la base de datos</li>
     * <li>Convierte la nueva empresa en {@link CompanyDTO} utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param companyCreate {@link CompanyCreate} con los datos de la nueva
     * empresa.
     * @return {@link CompanyDTO} Datos de la nueva empresa creada.
     * @throws BadRequestException Si la dirección no es válida.
     * @see UserService#getUser(java.util.UUID)
     * @see GeocodingService#obtenerDireccion(double, double)
     * @see WorkerService#createWorker(User, Company, Set)
     * @see CompanyMapper#toDTO(Company)
     * @see CompanyRepository#save(Company)
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
     * Obtiene los detalles de una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante
     * {@link CompanyRepository}.</li>
     * <li>Si no se encuentra, lanza una excepción {@link BadRequestException}
     * con mensaje descriptivo.</li>
     * <li>Convierte la empresa en un DTO con sus datos utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere obtener.
     * @return {@link CompanyDTO} Datos de la empresa con el UUID especificado.
     * @throws BadRequestException Si la empresa no existe.
     * @see CompanyRepository#findByUuid(UUID)
     * @see CompanyMapper#toDTO(Company)
     * @see BadRequestException
     */
    public CompanyDTO getCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        return companyMapper.toDTO(company);
    }

    public Company getCompanyEntity(UUID uuid) {
        return companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
    }

    public ListResponse<CompanyDTO> toListResponse(List<Company> companies) {
        return new ListResponse<>(companyMapper.toDTOList(companies));
    }

    /**
     * Actualiza los detalles de una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante
     * {@link CompanyRepository}.</li>
     * <li>Si no se encuentra, lanza una excepción {@link NotFoundException} con
     * mensaje descriptivo.</li>
     * <li>Si el usuario no es el dueño de la empresa, lanza una excepción
     * {@link BadRequestException} con mensaje descriptivo.</li>
     * <li>Convierte las coordenadas en una dirección a partir de
     * {@link GeocodingService}.</li>
     * <li>Si la dirección no es válida, lanza una excepción
     * {@link BadRequestException} con mensaje descriptivo.</li>
     * <li>Actualiza los campos de la empresa con los datos proporcionados.</li>
     * <li>Guarda la empresa actualizada en la base de datos.</li>
     * <li>Convierte la empresa en {@link CompanyDTO} utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere actualizar.
     * @param companyCreate {@link CompanyCreate} con los nuevos datos de la
     * empresa.
     * @return {@link CompanyDTO} Datos de la empresa actualizada.
     * @throws NotFoundException Si la empresa no existe.
     * @throws BadRequestException Si el usuario no es el dueño de la empresa.
     * @throws BadRequestException Si la dirección no es válida.
     * @see CompanyMapper#toDTO(Company)
     * @see CompanyRepository#findByUuid(UUID)
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
     * Elimina una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante su UUID</li>
     * <li>Si la empresa no existe, lanza una excepción
     * {@link NotFoundException} con mensaje descriptivo.</li>
     * <li>Corrobora que la empresa sea propiedad del usuario actual</li>
     * <li>Si no es el dueño lanza una excepción {@link BadRequestException} con
     * mensaje descriptivo.</li>
     * <li>Desactiva la empresa en la base de datos.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere eliminar.
     * @throws NotFoundException con mensaje descriptivo.
     * @throws BadRequestException con mensaje descriptivo.
     * @see CompanyRepository#findByUuid(UUID)
     */
    @Transactional
    public void deleteCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        company.setActive(false);
        companyRepository.save(company);
    }

}
