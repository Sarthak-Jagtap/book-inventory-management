package com.bookinventory.user.repository;

import com.bookinventory.user.entity.PermRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermRoleRepository extends JpaRepository<PermRole, Integer> {
	Optional<PermRole> findByPermRole(String permRole);

	boolean existsByPermRole(String permRole);
}