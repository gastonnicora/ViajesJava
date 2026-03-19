package com.gastonnicora.trips.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.dtos.request.User.UserPassword;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.dtos.request.User.UserRole;
import com.gastonnicora.trips.entitys.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    
    @Autowired
    private UserMapper userMapper;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTOs createUser(UserCreate user) {
        if (emailUsed(user.getEmail())) {
            throw new RuntimeException("El email ya esta siendo utilizado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = new User(null,
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                Set.of(Role.USER),
                true,
                null,
                null);

        return userMapper.toDTO(userRepository.save(newUser));
    }

    public UserDTOs getCurrentUser() {
        String email = getCurrentUserEmail();
        Optional<User> user = userRepository.findByEmailAndEnabled(email,true);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        throw new RuntimeException("El usuario no se encontró");

    }

    public UserDTOs getUserByUuid(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        throw new RuntimeException("El usuario buscado no existe");

    }

    public UserDTOs putUserByUuid(UUID uuid, UserPut user) {
        User userNow = userRepository.findByUuid(uuid).orElseThrow(
            ()-> 
            new RuntimeException("El usuario no existe"));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        if (!user.getEmail().equals(userNow.getEmail())) {
            if(emailUsed(user.getEmail())){
                throw new RuntimeException("El email ya esta en uso");
            }
            userNow.setEmail(user.getEmail());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);   

    }
    public UserDTOs putCurrentUser(UserPut user) {
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(),true).orElseThrow(
            ()-> 
            new RuntimeException("El usuario no existe"));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        if (!user.getEmail().equals(userNow.getEmail())) {
            if(emailUsed(user.getEmail())){
                throw new RuntimeException("El email ya esta en uso");
            }
            userNow.setEmail(user.getEmail());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);   

    }

    public UserDTOs updatePassword(UserPassword user){
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(), true).orElseThrow(
            ()-> 
            new RuntimeException("El usuario no existe")
        );
        if (!passwordEncoder.matches(user.getPasswordOld(), userNow.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        userNow.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);
    }

    public UserDTOs setRole(UUID uuid, UserRole role){
        User user = userRepository.findByUuid(uuid).orElseThrow(
            ()-> 
            new RuntimeException("El usuario no existe")
        );
        if (!user.getRole().contains(Role.SUPER_ADMIN)){
            role.getRoles().remove(Role.SUPER_ADMIN);
        }else{
            throw new RuntimeException("No se puede modificar los roles el SUPER_ADMIN");
        }
        user.setRole(role.getRoles());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    public void deleteCurrentUser(){
        User user= userRepository.findByEmailAndEnabled(getCurrentUserEmail(),true).orElseThrow(
            ()-> 
            new RuntimeException("El usuario no existe")
        );
        user.setEnabled(false);
        userRepository.save(user);
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }

    private Boolean emailUsed(String email) {
        User userEmail = userRepository.findByEmailAndEnabled(email,true).orElse(null);
        return (userEmail != null && userEmail.isEnabled());
    }
}
