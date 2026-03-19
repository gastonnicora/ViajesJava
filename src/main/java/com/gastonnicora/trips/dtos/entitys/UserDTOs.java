package com.gastonnicora.trips.dtos.entitys;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.Role;

public class UserDTOs {
    
    private UUID uuid;
    private String name;
    private String lastname;
    private String email;
    private Set<Role> role;
    private boolean enabled;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public UserDTOs(UUID uuid, String name, String lastname, String email, Set<Role> role, boolean enabled,
            LocalDate createdAt, LocalDate updatedAt) {
        this.uuid = uuid;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRole() {
        return role;
    }

    public void setRole(Set<Role> role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    

    
    

}
