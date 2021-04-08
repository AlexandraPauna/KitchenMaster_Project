package com.example.project.service;

import com.example.project.model.Role;

public interface RoleService {
    Role findRoleByUserName(String role_name);
}