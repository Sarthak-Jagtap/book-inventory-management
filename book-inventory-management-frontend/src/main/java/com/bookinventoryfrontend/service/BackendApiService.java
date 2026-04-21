package com.bookinventoryfrontend.service;

import com.bookinventoryfrontend.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class BackendApiService {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public BackendApiService(RestClient restClient, ObjectMapper objectMapper) {
		this.restClient = restClient;
		this.objectMapper = objectMapper;
	}

	// ═══════════════════════════════════════════════════════════
	// ── AUTH ENDPOINTS (no JWT needed)
	// ═══════════════════════════════════════════════════════════

	/**
	 * POST /api/v1/auth/login Returns: LoginResponseDTO containing token + user
	 * info
	 */
	public LoginResponseDTO login(String userName, String password) {
		LoginRequestDTO requestBody = new LoginRequestDTO(userName, password);

		Map<String, Object> response = restClient.post().uri("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON).body(requestBody).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, LoginResponseDTO.class);
	}

	/**
	 * POST /api/v1/auth/register Returns: UserResponseDTO of newly created user
	 */
	public UserResponseDTO register(RegisterRequestDTO dto) {
		Map<String, Object> response = restClient.post().uri("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON).body(dto).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, UserResponseDTO.class);
	}

	/**
	 * POST /api/v1/auth/validate-token Returns: Map with
	 * valid/userName/userId/roleName/message
	 */
	public Map<String, Object> validateToken(String token) {
		Map<String, Object> requestBody = Map.of("token", token);

		Map<String, Object> response = restClient.post().uri("/api/v1/auth/validate-token")
				.contentType(MediaType.APPLICATION_JSON).body(requestBody).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsMap(response);
	}

	// ═══════════════════════════════════════════════════════════
	// ── ROLE ENDPOINTS (public, no JWT needed)
	// ═══════════════════════════════════════════════════════════

	/**
	 * GET /api/v1/roles Returns list of all roles
	 */
	public List<PermRoleResponseDTO> getAllRoles() {
		Map<String, Object> response = restClient.get().uri("/api/v1/roles").retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, PermRoleResponseDTO.class);
	}

	/**
	 * GET /api/v1/roles/{roleNumber}
	 */
	public PermRoleResponseDTO getRoleById(Integer roleNumber) {
		Map<String, Object> response = restClient.get().uri("/api/v1/roles/{id}", roleNumber).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, PermRoleResponseDTO.class);
	}

	/**
	 * GET /api/v1/roles/{roleNumber}/user-count
	 */
	public Map<String, Object> getRoleUserCount(Integer roleNumber) {
		Map<String, Object> response = restClient.get().uri("/api/v1/roles/{id}/user-count", roleNumber).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsMap(response);
	}

	// ═══════════════════════════════════════════════════════════
	// ── USER PROFILE ENDPOINTS (JWT required)
	// ═══════════════════════════════════════════════════════════

	/**
	 * GET /api/v1/user/profile Requires: Bearer token in Authorization header
	 */
	public UserResponseDTO getMyProfile(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/user/profile")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, UserResponseDTO.class);
	}

	/**
	 * GET /api/v1/user/dashboard Returns: profile + purchase count + inventory IDs
	 * in one shot
	 */
	public UserDashboardDTO getMyDashboard(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/user/dashboard")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, UserDashboardDTO.class);
	}

	/**
	 * GET /api/v1/user/purchases
	 */
	public List<PurchaseLogResponseDTO> getMyPurchases(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/user/purchases")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, PurchaseLogResponseDTO.class);
	}

	/**
	 * GET /api/v1/user/purchases/count
	 */
	public Long getMyPurchaseCount(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/user/purchases/count")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		Object data = response.get("data");
		if (data instanceof Number)
			return ((Number) data).longValue();
		return 0L;
	}

	/**
	 * GET /api/v1/user/purchases/check/{inventoryId}
	 */
	public Boolean checkMyPurchase(String jwtToken, Integer inventoryId) {
		Map<String, Object> response = restClient.get().uri("/api/v1/user/purchases/check/{id}", inventoryId)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		Object data = response.get("data");
		if (data instanceof Boolean)
			return (Boolean) data;
		return false;
	}

	// ═══════════════════════════════════════════════════════════
	// ── STORE OWNER ENDPOINTS (JWT required — StoreOwner/Admin)
	// ═══════════════════════════════════════════════════════════

	/**
	 * GET /api/v1/store-owner/purchases
	 */
	public List<PurchaseLogResponseDTO> getAllPurchases(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/store-owner/purchases")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, PurchaseLogResponseDTO.class);
	}

	/**
	 * GET /api/v1/store-owner/purchases/user/{userId}
	 */
	public List<PurchaseLogResponseDTO> getPurchasesByUser(String jwtToken, Integer userId) {
		Map<String, Object> response = restClient.get().uri("/api/v1/store-owner/purchases/user/{id}", userId)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, PurchaseLogResponseDTO.class);
	}

	/**
	 * GET /api/v1/store-owner/purchases/inventory/{inventoryId}
	 */
	public List<PurchaseLogResponseDTO> getPurchasesByInventory(String jwtToken, Integer inventoryId) {
		Map<String, Object> response = restClient.get().uri("/api/v1/store-owner/purchases/inventory/{id}", inventoryId)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, PurchaseLogResponseDTO.class);
	}

	/**
	 * GET /api/v1/store-owner/purchases/stats
	 */
	public Map<String, Object> getPurchaseStats(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/store-owner/purchases/stats")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsMap(response);
	}

	/**
	 * GET /api/v1/store-owner/purchases/top-buyers?limit=5
	 */
	public List<Map<String, Object>> getTopBuyers(String jwtToken, int limit) {
		Map<String, Object> response = restClient.get()
				.uri("/api/v1/store-owner/purchases/top-buyers?limit={limit}", limit)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsListOfMaps(response);
	}

	// ═══════════════════════════════════════════════════════════
	// ── ADMIN ENDPOINTS (JWT required — Admin only)
	// ═══════════════════════════════════════════════════════════

	/**
	 * GET /api/v1/admin/users
	 */
	public List<UserResponseDTO> getAllUsers(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/admin/users")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, UserResponseDTO.class);
	}

	/**
	 * GET /api/v1/admin/users/{userId}
	 */
	public UserResponseDTO getUserById(String jwtToken, Integer userId) {
		Map<String, Object> response = restClient.get().uri("/api/v1/admin/users/{id}", userId)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractData(response, UserResponseDTO.class);
	}

	/**
	 * GET /api/v1/admin/users/search?firstName=X&lastName=Y
	 */
	public List<UserResponseDTO> searchUsers(String jwtToken, String firstName, String lastName) {
		StringBuilder uri = new StringBuilder("/api/v1/admin/users/search?");
		if (firstName != null && !firstName.isBlank())
			uri.append("firstName=").append(firstName).append("&");
		if (lastName != null && !lastName.isBlank())
			uri.append("lastName=").append(lastName);

		Map<String, Object> response = restClient.get().uri(uri.toString())
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, UserResponseDTO.class);
	}

	/**
	 * GET /api/v1/admin/users/by-role/{roleNumber}
	 */
	public List<UserResponseDTO> getUsersByRole(String jwtToken, Integer roleNumber) {
		Map<String, Object> response = restClient.get().uri("/api/v1/admin/users/by-role/{roleNumber}", roleNumber)
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsList(response, UserResponseDTO.class);
	}

	/**
	 * GET /api/v1/admin/dashboard
	 */
	public Map<String, Object> getAdminDashboard(String jwtToken) {
		Map<String, Object> response = restClient.get().uri("/api/v1/admin/dashboard")
				.header("Authorization", "Bearer " + jwtToken).retrieve()
				.body(new ParameterizedTypeReference<Map<String, Object>>() {
				});

		return extractDataAsMap(response);
	}

	// ═══════════════════════════════════════════════════════════
	// ── PRIVATE HELPER METHODS
	// ═══════════════════════════════════════════════════════════

	/**
	 * Extracts the "data" field from the backend ApiResponse wrapper and converts
	 * it to the given target class using Jackson.
	 */
	@SuppressWarnings("unchecked")
	private <T> T extractData(Map<String, Object> response, Class<T> targetClass) {
		if (response == null)
			throw new RuntimeException("No response from backend");

		// Check if backend returned success
		Boolean success = (Boolean) response.get("success");
		if (Boolean.FALSE.equals(success)) {
			String msg = (String) response.getOrDefault("message", "Backend returned an error");
			throw new RuntimeException(msg);
		}

		Object data = response.get("data");
		if (data == null)
			return null;

		// Jackson converts the LinkedHashMap (from JSON) to the target DTO class
		return objectMapper.convertValue(data, targetClass);
	}

	/**
	 * Extracts "data" as a List of DTOs
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> extractDataAsList(Map<String, Object> response, Class<T> targetClass) {
		if (response == null)
			throw new RuntimeException("No response from backend");

		Boolean success = (Boolean) response.get("success");
		if (Boolean.FALSE.equals(success)) {
			String msg = (String) response.getOrDefault("message", "Backend returned an error");
			throw new RuntimeException(msg);
		}

		Object data = response.get("data");
		if (data == null)
			return List.of();

		return objectMapper.convertValue(data,
				objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
	}

	/**
	 * Extracts "data" as a raw Map (for endpoints returning custom objects)
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> extractDataAsMap(Map<String, Object> response) {
		if (response == null)
			throw new RuntimeException("No response from backend");
		Object data = response.get("data");
		if (data instanceof Map)
			return (Map<String, Object>) data;
		return Map.of();
	}

	/**
	 * Extracts "data" as a List of Maps (for endpoints returning List of custom
	 * objects)
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> extractDataAsListOfMaps(Map<String, Object> response) {
		if (response == null)
			return List.of();
		Object data = response.get("data");
		if (data instanceof List)
			return (List<Map<String, Object>>) data;
		return List.of();
	}

	// ══════════════════════════════════════════════════════════════
	// ADD THESE METHODS TO BackendApiService.java
	// ══════════════════════════════════════════════════════════════

	// ── 1. Add a getter for RestClient (needed by controller) ─────
	public RestClient getRestClient() {
		return this.restClient;
	}

	// ── 2. Change Password ─────────────────────────────────────────
	/**
	 * PATCH /api/v1/user/change-password
	 */
	public void changePassword(String jwtToken, String currentPassword, String newPassword, String confirmPassword) {

		java.util.Map<String, String> body = new java.util.LinkedHashMap<>();
		body.put("currentPassword", currentPassword);
		body.put("newPassword", newPassword);
		body.put("confirmPassword", confirmPassword);

		java.util.Map<String, Object> response = restClient.patch().uri("/api/v1/user/change-password")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON).body(body).retrieve()
				.body(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {
				});

		if (response != null && Boolean.FALSE.equals(response.get("success"))) {
			String msg = (String) response.getOrDefault("message", "Change password failed");
			throw new RuntimeException(msg);
		}
	}

	// ── 3. Update My Profile ───────────────────────────────────────
	/**
	 * PATCH /api/v1/user/profile Only sends fields that are non-null and non-blank.
	 */
	public UserResponseDTO updateMyProfile(String jwtToken, String firstName, String lastName, String userName,
			String phoneNumber) {

		java.util.Map<String, String> body = new java.util.LinkedHashMap<>();
		if (firstName != null && !firstName.isBlank())
			body.put("firstName", firstName);
		if (lastName != null && !lastName.isBlank())
			body.put("lastName", lastName);
		if (userName != null && !userName.isBlank())
			body.put("userName", userName);
		if (phoneNumber != null && !phoneNumber.isBlank())
			body.put("phoneNumber", phoneNumber);

		java.util.Map<String, Object> response = restClient.patch().uri("/api/v1/user/profile")
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON).body(body).retrieve()
				.body(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {
				});

		return extractData(response, UserResponseDTO.class);
	}

	// ── 4. Update User Role (Admin) ────────────────────────────────
	/**
	 * PATCH /api/v1/admin/users/{userId}/role
	 */
	public UserResponseDTO updateUserRole(String jwtToken, Integer userId, Integer roleNumber) {

		java.util.Map<String, Integer> body = java.util.Map.of("roleNumber", roleNumber);

		java.util.Map<String, Object> response = restClient.patch().uri("/api/v1/admin/users/{id}/role", userId)
				.header("Authorization", "Bearer " + jwtToken)
				.contentType(org.springframework.http.MediaType.APPLICATION_JSON).body(body).retrieve()
				.body(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {
				});

		return extractData(response, UserResponseDTO.class);
	}
}