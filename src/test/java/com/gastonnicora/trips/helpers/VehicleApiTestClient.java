package com.gastonnicora.trips.helpers;

import java.util.UUID;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;

import tools.jackson.databind.ObjectMapper;


public class VehicleApiTestClient {

    private final MockMvc mockMvc;
    private String token;

    private final ObjectMapper objectMapper;

    public VehicleApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public VehicleApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions getVehicle(UUID companyUuid,UUID uuid)
            throws Exception {

        return mockMvc.perform(get("/api/companies/" + companyUuid + "/vehicle/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    
    public ResultActions createVehicle(UUID companyUuid, String plate, String model, Integer capacity) throws Exception {

        VehicleCreate body = new VehicleCreate(
                plate,
                model,
                capacity
        );

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(post("/api/companies/" + companyUuid + "/vehicle")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
    }
    public ResultActions deleteVehicle(UUID companyUuid, UUID vehicleUuid)
            throws Exception { 

        return mockMvc.perform(delete(
                "/api/companies/" + companyUuid + "/vehicle/" + vehicleUuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

}
