package com.bookinventory.user.service;

import com.bookinventory.user.common.exception.BadRequestException;
import com.bookinventory.user.common.exception.DuplicateResourceException;
import com.bookinventory.user.common.exception.InvalidCredentialsException;
import com.bookinventory.user.common.exception.ResourceNotFoundException;
import com.bookinventory.user.dto.ChangePasswordRequestDTO;
import com.bookinventory.user.dto.LoginRequestDTO;
import com.bookinventory.user.dto.LoginResponseDTO;
import com.bookinventory.user.dto.PermRoleResponseDTO;
import com.bookinventory.user.dto.UserRequestDTO;
import com.bookinventory.user.dto.UserResponseDTO;
import com.bookinventory.user.dto.UserUpdateRequestDTO;
import com.bookinventory.user.entity.PermRole;
import com.bookinventory.user.entity.User;
import com.bookinventory.user.repository.PermRoleRepository;
import com.bookinventory.user.repository.PurchaseLogRepository;
import com.bookinventory.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository        userRepository;
    private final PermRoleRepository    permRoleRepository;
    private final PurchaseLogRepository purchaseLogRepository;

    // Constructor injection
    public UserServiceImpl(UserRepository userRepository,
                           PermRoleRepository permRoleRepository,
                           PurchaseLogRepository purchaseLogRepository) {
        this.userRepository        = userRepository;
        this.permRoleRepository    = permRoleRepository;
        this.purchaseLogRepository = purchaseLogRepository;
    }

    private PermRoleResponseDTO convertRoleToDTO(PermRole role) {
        if (role == null) return null;
        PermRoleResponseDTO dto = new PermRoleResponseDTO();
        dto.setRoleNumber(role.getRoleNumber());
        dto.setPermRole(role.getPermRole());
        return dto;
    }

    private UserResponseDTO convertUserToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setLastName(user.getLastName());
        dto.setFirstName(user.getFirstName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUserName(user.getUserName());
        dto.setRole(convertRoleToDTO(user.getRole()));
        return dto;
    }

    // REGISTER USER
    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO dto) {

        // 1. Check if username is already taken
        if (userRepository.existsByUserName(dto.getUserName())) {
            throw new DuplicateResourceException(
                    "User", "userName", dto.getUserName());
        }

        // 2. Resolve role — use provided roleNumber, otherwise default to 1 (Guest)
        Integer roleNumberToUse = (dto.getRoleNumber() != null) ? dto.getRoleNumber() : 1;

        PermRole role = permRoleRepository.findById(roleNumberToUse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role", "roleNumber", roleNumberToUse));

        // 3. Build and save User entity
        User user = new User();
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return convertUserToDTO(savedUser);
    }

    // LOGIN
    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO dto) {

        // 1. Check username exists
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid username or password"));

        // 2. Check password matches
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // 3. Build login response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRoleName(user.getRole() != null
                ? user.getRole().getPermRole() : "Guest");
        response.setMessage("Login successful");

        return response;
    }

    // GET USER BY ID
    @Override
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId));
        return convertUserToDTO(user);
    }

    // GET USER BY USERNAME
    @Override
    public UserResponseDTO getUserByUsername(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userName", userName));
        return convertUserToDTO(user);
    }

    // GET ALL USERS
    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAllUsersWithRole();
        List<UserResponseDTO> dtoList = new ArrayList<>();
        for (User user : users) {
            dtoList.add(convertUserToDTO(user));
        }
        return dtoList;
    }
    
    // GET USERS BY ROLE
    @Override
    public List<UserResponseDTO> getUsersByRole(Integer roleNumber) {

        // Validate role exists first
        if (!permRoleRepository.existsById(roleNumber)) {
            throw new ResourceNotFoundException("Role", "roleNumber", roleNumber);
        }

        List<User> users = userRepository.findByRole_RoleNumber(roleNumber);
        List<UserResponseDTO> dtoList = new ArrayList<>();
        for (User user : users) {
            dtoList.add(convertUserToDTO(user));
        }
        return dtoList;
    }

    // UPDATE USER PROFILE
    @Override
    @Transactional
    public UserResponseDTO updateUser(Integer userId, UserUpdateRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId));

        // Only update fields that are actually provided (not null)
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
            // Check new username is not already taken by someone else
            if (userRepository.existsByUserName(dto.getUserName()) &&
                !user.getUserName().equals(dto.getUserName())) {
                throw new DuplicateResourceException(
                        "User", "userName", dto.getUserName());
            }
            user.setUserName(dto.getUserName());
        }

        User updatedUser = userRepository.save(user);
        return convertUserToDTO(updatedUser);
    }

    // CHANGE PASSWORD
    @Override
    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId));

        // 1. Verify current password is correct
        if (!user.getPassword().equals(dto.getCurrentPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // 2. New password and confirm password must match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException(
                    "New password and confirm password do not match");
        }

        // 3. New password must not be the same as current
        if (dto.getNewPassword().equals(dto.getCurrentPassword())) {
            throw new BadRequestException(
                    "New password must be different from current password");
        }

        // 4. Update password using the targeted repository query
        userRepository.updatePassword(userId, dto.getNewPassword());
    }

    // UPDATE USER ROLE (Admin operation)
    @Override
    @Transactional
    public UserResponseDTO updateUserRole(Integer userId, Integer roleNumber) {

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "userId", userId));

        // Validate role exists
        PermRole role = permRoleRepository.findById(roleNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role", "roleNumber", roleNumber));

        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return convertUserToDTO(updatedUser);
    }

    // DELETE USER
    @Override
    @Transactional
    public void deleteUser(Integer userId) {

        // 1. Check user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "userId", userId);
        }

        // 2. Delete all purchase logs for this user first (FK constraint)
        purchaseLogRepository.deleteById_UserId(userId);

        // 3. Delete the user
        userRepository.deleteById(userId);
    }
}