package com.gastonnicora.trips.integration.worker;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetWorkersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    private WorkerApiTestClient workerApi;

    private UserDTO user;
    private UUID companyUuid;

    @Autowired
    private ObjectMapper objectMapper;

    private final String name = "user";
    private final String pass = "goodPassword";

    @BeforeEach
    void setup() throws Exception {
        user = UserTestFactory.registerUser(mockMvc, name, pass);

        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                pass
        );

        workerApi = new WorkerApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());

        companyUuid = new CompanyTestFactory(mockMvc, objectMapper, login.getToken(), geocodingService).createCompany().getUuid();
    }

    private WorkerApiTestClient loginAs(UserDTO user) throws Exception {
        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                pass
        );

        return new WorkerApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());
    }

    private UserDTO createWorkerWithRole(RoleCompany role) throws Exception {
        UserDTO worker = UserTestFactory.registerUser(
                mockMvc,
                "worker_" + role.name(),
                pass
        );

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(role)
                )
                .andExpect(status().isOk());

        return worker;
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsOwner() throws Exception {
        workerApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsAdmin() throws Exception {
        UserDTO admin = createWorkerWithRole(RoleCompany.ADMIN);

        WorkerApiTestClient adminApi = loginAs(admin);

        adminApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsHrManager() throws Exception {
        UserDTO hrManager = createWorkerWithRole(RoleCompany.HR_MANAGER);

        WorkerApiTestClient hrManagerApi = loginAs(hrManager);

        hrManagerApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToGetWorkers() throws Exception {
        UserDTO driver = createWorkerWithRole(RoleCompany.DRIVER);

        WorkerApiTestClient driverApi = loginAs(driver);

        driverApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenSellerTriesToGetWorkers() throws Exception {
        UserDTO seller = createWorkerWithRole(RoleCompany.SELLER);

        WorkerApiTestClient sellerApi = loginAs(seller);

        sellerApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToGetWorkers() throws Exception {
        UserDTO normalUser = UserTestFactory.registerUser(
                mockMvc,
                "normal_user",
                pass
        );

        WorkerApiTestClient userApi = loginAs(normalUser);

        userApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        WorkerApiTestClient unauthorizedApi = new WorkerApiTestClient(
                mockMvc,
                new ObjectMapper()
        );

        unauthorizedApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        workerApi
                .getWorkersByCompany(UUID.randomUUID())
                .andExpect(status().isForbidden());
    }
}
