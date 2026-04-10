package com.bookinventory.user.repository;

import com.bookinventory.user.entity.PermRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PermRoleRepository extends JpaRepository<PermRole, Integer> {

    // Find a role by its name
    Optional<PermRole> findByPermRole(String permRole);

    // Check if a role name already exists
    boolean existsByPermRole(String permRole);
}