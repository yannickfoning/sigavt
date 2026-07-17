package com.sigavt.repository;

import com.sigavt.entity.Role;
import com.sigavt.enums.RoleNom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(RoleNom nom);
}
