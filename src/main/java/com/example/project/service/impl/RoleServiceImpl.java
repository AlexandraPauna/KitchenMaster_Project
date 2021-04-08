package com.example.project.service.impl;

import com.example.project.model.Role;
import com.example.project.repository.RoleRepository;
import com.example.project.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findRoleByUserName(String role_name) {
        return roleRepository.findByRole(role_name);
    }
}
