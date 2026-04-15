package com.bookinventory.user.service;

import com.bookinventory.common.exception.*;
import com.bookinventory.user.dto.*;
import com.bookinventory.user.entity.*;
import com.bookinventory.user.repository.*;
import com.bookinventory.user.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PermRoleRepository permRoleRepository;
	private final PurchaseLogRepository purchaseLogRepository;
	private final JwtUtil jwtUtil;

	public UserServiceImpl(UserRepository userRepository, PermRoleRepository permRoleRepository,
			PurchaseLogRepository purchaseLogRepository, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.permRoleRepository = permRoleRepository;
		this.purchaseLogRepository = purchaseLogRepository;
		this.jwtUtil = jwtUtil;
	}

	// Private helpers

	private PermRoleResponseDTO convertRoleToDTO(PermRole role) {
		if (role == null)
			return null;
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

	// AUTH

	@Override
	@Transactional
	public UserResponseDTO registerUser(UserRequestDTO dto) {

		// 1. Username must be unique among ACTIVE users
		if (userRepository.existsByUserName(dto.getUserName())) {
		    throw new DuplicateResourceException("User", "userName", dto.getUserName());
		}

		// 2. Resolve role — default to 2 (RegisteredUser) for self-registration
		// Only Admin can assign StoreOwner(3) or Admin(4)
		Integer roleNumberToUse = (dto.getRoleNumber() != null) ? dto.getRoleNumber() : 2;

		PermRole role = permRoleRepository.findById(roleNumberToUse)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "roleNumber", roleNumberToUse));

		// 3. Build and save
		User user = new User();
		user.setLastName(dto.getLastName());
		user.setFirstName(dto.getFirstName());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.setUserName(dto.getUserName());
		user.setPassword(dto.getPassword());
		user.setRole(role);

		return convertUserToDTO(userRepository.save(user));
	}

	@Override
	public LoginResponseDTO loginUser(LoginRequestDTO dto) {

		// 1. Must exist AND be active
		User user = userRepository.findByUserName(dto.getUserName())
		        .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

		// 2. Password check
		if (!user.getPassword().equals(dto.getPassword())) {
			throw new InvalidCredentialsException("Invalid username or password");
		}

		// 3. Build JWT
		String roleName = user.getRole() != null ? user.getRole().getPermRole() : "Guest";
		String token = jwtUtil.generateToken(user.getUserName(), user.getUserId(), roleName);

		// 4. Build response
		LoginResponseDTO response = new LoginResponseDTO();
		response.setUserId(user.getUserId());
		response.setUserName(user.getUserName());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setRoleName(roleName);
		response.setMessage("Login successful");
		response.setToken(token);
		response.setTokenType("Bearer");

		return response;
	}

	// OWN PROFILE (RegisteredUser)

	@Override
	public UserResponseDTO getMyProfile(Integer userId) {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		return convertUserToDTO(user);
	}

	@Override
	@Transactional
	public UserResponseDTO updateMyProfile(Integer userId, UserUpdateRequestDTO dto) {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		if (dto.getLastName() != null && !dto.getLastName().isBlank())
			user.setLastName(dto.getLastName());
		if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
			user.setFirstName(dto.getFirstName());
		if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank())
			user.setPhoneNumber(dto.getPhoneNumber());
		if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
			if (userRepository.existsByUserName(dto.getUserName())
			        && !user.getUserName().equals(dto.getUserName())) {
				throw new DuplicateResourceException("User", "userName", dto.getUserName());
			}
			user.setUserName(dto.getUserName());
		}

		return convertUserToDTO(userRepository.save(user));
	}

	@Override
	@Transactional
	public void changeMyPassword(Integer userId, ChangePasswordRequestDTO dto) {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		if (!user.getPassword().equals(dto.getCurrentPassword()))
			throw new BadRequestException("Current password is incorrect");

		if (!dto.getNewPassword().equals(dto.getConfirmPassword()))
			throw new BadRequestException("New password and confirm password do not match");

		if (dto.getNewPassword().equals(dto.getCurrentPassword()))
			throw new BadRequestException("New password must be different from current password");

		userRepository.updatePassword(userId, dto.getNewPassword());
	}

	// ADMIN — all users

	@Override
	public List<UserResponseDTO> getAllUsers() {
		List<UserResponseDTO> result = new ArrayList<>();
		for (User u : userRepository.findAllUsersWithRole())
			result.add(convertUserToDTO(u));
		return result;
	}

	@Override
	public UserResponseDTO getUserById(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		return convertUserToDTO(user);
	}

	@Override
	@Transactional
	public UserResponseDTO updateUserById(Integer userId, UserUpdateRequestDTO dto) {
		// Admin can update any user — same logic as updateMyProfile but no active check
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		if (dto.getLastName() != null && !dto.getLastName().isBlank())
			user.setLastName(dto.getLastName());
		if (dto.getFirstName() != null && !dto.getFirstName().isBlank())
			user.setFirstName(dto.getFirstName());
		if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank())
			user.setPhoneNumber(dto.getPhoneNumber());
		if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
			if (userRepository.existsByUserName(dto.getUserName())
			        && !user.getUserName().equals(dto.getUserName())) {
			    throw new DuplicateResourceException("User", "userName", dto.getUserName());
			}
			user.setUserName(dto.getUserName());
		}
		return convertUserToDTO(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserResponseDTO updateUserRole(Integer userId, Integer roleNumber) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		PermRole role = permRoleRepository.findById(roleNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "roleNumber", roleNumber));

		user.setRole(role);
		return convertUserToDTO(userRepository.save(user));
	}
	
	// ── Shared ────────────────────────────────────────────────────

	@Override
	public List<UserResponseDTO> getUsersByRole(Integer roleNumber) {
		if (!permRoleRepository.existsById(roleNumber))
			throw new ResourceNotFoundException("Role", "roleNumber", roleNumber);

		List<UserResponseDTO> result = new ArrayList<>();
		for (User u : userRepository.findByRole_RoleNumber(roleNumber))
			result.add(convertUserToDTO(u));
		return result;
	}
}