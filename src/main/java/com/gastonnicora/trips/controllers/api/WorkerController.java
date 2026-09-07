package com.gastonnicora.trips.controllers.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.services.WorkerService;

import io.swagger.v3.oas.annotations.tags.Tag;

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

    public WorkerController(WorkerService workerService){
        this.workerService = workerService;
    }

}
