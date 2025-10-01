package com.example.spring_boot.Model.user;

import java.util.Set;

/**
 * Represents the roles available in the PunchLite system.
 * -Includes EMPLOYEE, ADMIN, and MANAGER roles.
 * - Each role has a specific set of permissions associated with it.
 * 
 */
public enum Role {
    PLAYER(Set.of(Permission.READ_SELF));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions(){
        return permissions;
    }
}