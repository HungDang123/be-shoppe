package com.project.shopapp.services;

import com.project.shopapp.models.Role;
import com.project.shopapp.repositories.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Service
public class RoleService implements IRoleService {
    RoleRepository roleRepository;
    @Override
    public List<Role> roles() {
        return roleRepository.findAll();
    }
}
