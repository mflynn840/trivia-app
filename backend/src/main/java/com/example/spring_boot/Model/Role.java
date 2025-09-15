package com.example.spring_boot.Model;

import java.util.Set;

/**
 * Represents the roles available in the PunchLite system.
 * -Includes EMPLOYEE, ADMIN, and MANAGER roles.
 * - Each role has a specific set of permissions associated with it.
 * 
 */
public enum Role {
    USER(Set.of(Permission.READ_SELF)),
    EMPLOYEE(Set.of(Permission.READ_SELF)),
    ADMIN(Set.of(
        Permission.READ_SELF,
        Permission.READ_ALL_EMPLOYEES,
        Permission.EDIT_ALL_TIMECARDS,
        Permission.VALIDATE_ALL_TIMECARDS,
        Permission.SET_ALL_WAGES
    )),
    MANAGER(Set.of(
        Permission.READ_SELF,
        Permission.EDIT_EMPLOYEE_TIMECARDS,
        Permission.VALIDATE_TIMECARDS,
        Permission.SET_EMPLOYEE_WAGES
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions(){
        return permissions;
    }
}
