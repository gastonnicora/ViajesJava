package com.gastonnicora.trips.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.UserRepository;

/**
 * Implementación de {@link UserDetailsService} de Spring Security.
 *
 * <p>
 * Se encarga de cargar la información de un usuario desde la base de datos
 * para su autenticación.
 * </p>
 *
 * <p>
 * Utiliza {@link UserRepository} para buscar usuarios habilitados por correo
 * electrónico y retorna un {@link UserDetailsImpl} que permite a Spring
 * Security gestionar la autenticación y autorización.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario para el proceso de autenticación.
     *
     * <p>
     * En esta implementación, el nombre de usuario corresponde al correo
     * electrónico del usuario. La búsqueda se realiza únicamente entre los
     * usuarios habilitados.
     * </p>
     *
     * @param username Correo electrónico del usuario que se desea autenticar.
     * @return {@link UserDetails} con la información del usuario encontrado.
     * @throws UsernameNotFoundException Si no existe un usuario habilitado con el
     *                                   correo electrónico indicado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserDetailsImpl(user);
    }

}