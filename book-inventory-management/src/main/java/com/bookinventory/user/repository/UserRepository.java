package com.bookinventory.user.repository;

import com.bookinventory.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Auth
    // Find active user by username (used at login)
    Optional<User> findByUserNameAndActiveTrue(String userName);

    // Check if username already taken (among active users)
    boolean existsByUserNameAndActiveTrue(String userName);

    // Admin queries

    // Get ALL users (including inactive) — Admin only
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    List<User> findAllUsersWithRole();

    // Get only ACTIVE users with role — Admin only
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.active = true")
    List<User> findAllActiveUsersWithRole();

    // Get users by role number (active only)
    List<User> findByRole_RoleNumberAndActiveTrue(Integer roleNumber);

    // PATCH queries

    // Soft delete — set active = false
    @Modifying
    @Query("UPDATE User u SET u.active = :status WHERE u.userId = :userId")
    int updateActiveStatus(@Param("userId") Integer userId,
                           @Param("status") boolean status);

    // Update password
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.userId = :userId")
    int updatePassword(@Param("userId") Integer userId,
                       @Param("newPassword") String newPassword);

    // Update role
    @Modifying
    @Query("UPDATE User u SET u.role = :role WHERE u.userId = :userId")
    int updateUserRole(@Param("userId") Integer userId,
                       @Param("role") com.bookinventory.user.entity.PermRole role);

    // Search
    List<User> findByLastNameIgnoreCaseAndActiveTrue(String lastName);
    List<User> findByFirstNameIgnoreCaseAndActiveTrue(String firstName);
}