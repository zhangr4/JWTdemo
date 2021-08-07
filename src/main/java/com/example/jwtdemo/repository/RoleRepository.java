package com.example.jwtdemo.repository;

import com.example.jwtdemo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String rolename);
}
