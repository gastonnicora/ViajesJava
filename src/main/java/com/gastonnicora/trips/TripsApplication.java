package com.gastonnicora.trips;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TripsApplication implements CommandLineRunner {

	public static void main(String[] args) {

        // Cargar .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
		
		SpringApplication.run(TripsApplication.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("Iniciando la aplicación de trips...");
		System.out.println("La aplicación de trips está lista para recibir solicitudes.");
	}

}
