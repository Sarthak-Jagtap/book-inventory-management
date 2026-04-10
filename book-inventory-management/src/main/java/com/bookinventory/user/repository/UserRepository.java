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

    // Login / Auth

    // Find user by username
    Optional<User> findByUserName(String userName);

    // Find user by username AND password
    Optional<User> findByUserNameAndPassword(String userName, String password);

    // Check if a username is already taken
    boolean existsByUserName(String userName);

    // Role-based Queries

    // Get all users who have a specific role number
    List<User> findByRole_RoleNumber(Integer roleNumber);

    // Search Queries

    // Find users by last name
    List<User> findByLastNameIgnoreCase(String lastName);

    // Find users by first name
    List<User> findByFirstNameIgnoreCase(String firstName);

    // Custom JPQL Queries

    // Update only the password for a specific user
    // @Modifying + @Transactional (on service) required for UPDATE/DELETE
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.userId = :userId")
    int updatePassword(@Param("userId") Integer userId,
                       @Param("newPassword") String newPassword);

    // Update the role of a specific user
    @Modifying
    @Query("UPDATE User u SET u.role.roleNumber = :roleNumber WHERE u.userId = :userId")
    int updateUserRole(@Param("userId") Integer userId,
                       @Param("roleNumber") Integer roleNumber);

    // Get all users along with their role info in one query
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    List<User> findAllUsersWithRole();
}