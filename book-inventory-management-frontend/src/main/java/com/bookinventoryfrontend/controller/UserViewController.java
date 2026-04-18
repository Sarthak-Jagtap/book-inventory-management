package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.*;
import com.bookinventoryfrontend.service.BackendApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserViewController
 *
 * Every endpoint card on the user-module page links to a route here.
 * This controller:
 *  1. Reads JWT token from session (stored by AuthViewController on login)
 *  2. Calls the backend via BackendApiService
 *  3. Puts the data into the Model
 *  4. Returns the Thymeleaf template name to render
 *
 * SESSION KEY REMINDER:
 *   session.getAttribute("JWT_TOKEN")  → Bearer token
 *   session.getAttribute("USER_ID")    → Integer userId
 *   session.getAttribute("USER_NAME")  → String username
 *   session.getAttribute("ROLE_NAME")  → String role
 */
@Controller
@RequestMapping("/user/api")
public class UserViewController {

    private final BackendApiService backendApiService;

    public UserViewController(BackendApiService backendApiService) {
        this.backendApiService = backendApiService;
    }

    // ── Helper: get JWT from session ─────────────────────────────
    private String getToken(HttpSession session) {
        Object token = session.getAttribute(AuthViewController.SESSION_JWT_TOKEN);
        if (token == null) throw new RuntimeException("Session expired. Please log in again.");
        return token.toString();
    }

    // ── Helper: add common breadcrumb data to model ──────────────
    private void addBreadcrumb(Model model, String pageName) {
        model.addAttribute("activePage", "user-module");
        model.addAttribute("currentPage", pageName);
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 1 — AUTH DEMOS
    // ═══════════════════════════════════════════════════════════

    /** GET /user/api/validate-token — Show token validation result */
    @GetMapping("/validate-token")
    public String validateToken(HttpSession session, Model model) {
        addBreadcrumb(model, "Validate Token");
        model.addAttribute("apiEndpoint", "POST /api/v1/auth/validate-token");
        model.addAttribute("apiDescription",
            "Sends the current session JWT token to the backend and checks if it is " +
            "still valid. Returns the token's embedded claims: username, user ID, and role name. " +
            "Useful for checking token expiry without making a protected API call.");

        try {
            // Get the JWT that's stored in the session right now
            String token = getToken(session);

            // Call the backend validate-token endpoint
            Map<String, Object> result = backendApiService.validateToken(token);
            model.addAttribute("tokenResult", result);
            model.addAttribute("tokenPreview",
                token.length() > 40 ? token.substring(0, 40) + "..." : token);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/validate-token"; // → templates/user/validate-token.html
    }

    /** GET /user/api/register-demo — Show the register API info page */
    @GetMapping("/register-demo")
    public String registerDemo(Model model) {
        addBreadcrumb(model, "Register API");
        model.addAttribute("apiEndpoint", "POST /api/v1/auth/register");
        model.addAttribute("apiDescription",
            "Registers a new user in the system. Validates all fields: " +
            "first name (max 20), last name (max 30), username (unique, max 30), " +
            "password (4–30 chars), phone number format (XXX) XXX-XXXX. " +
            "Returns the created user profile. Role defaults to RegisteredUser.");
        return "user/register-demo"; // → templates/user/register-demo.html
    }

    /** GET /user/api/login-demo — Show the login API info page */
    @GetMapping("/login-demo")
    public String loginDemo(Model model) {
        addBreadcrumb(model, "Login API");
        model.addAttribute("apiEndpoint", "POST /api/v1/auth/login");
        model.addAttribute("apiDescription",
            "Authenticates the user with username and password. " +
            "On success returns a signed JWT token (HS256) containing userId, " +
            "username, and roleName as claims. Token expiry is configurable. " +
            "Include this token as: Authorization: Bearer <token> in subsequent requests.");
        return "user/login-demo"; // → templates/user/login-demo.html
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 2 — ROLES (PUBLIC)
    // ═══════════════════════════════════════════════════════════

    /** GET /user/api/roles — List all roles */
    @GetMapping("/roles")
    public String getAllRoles(Model model) {
        addBreadcrumb(model, "All Roles");
        model.addAttribute("apiEndpoint", "GET /api/v1/roles");
        model.addAttribute("apiDescription",
            "Returns all permission roles defined in the system. " +
            "Roles control what each user can access. " +
            "Public endpoint — no login required. " +
            "Currently 4 roles: Guest (1), RegisteredUser (2), StoreOwner (3), Admin (4).");
        try {
            List<PermRoleResponseDTO> roles = backendApiService.getAllRoles();
            model.addAttribute("roles", roles);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/roles"; // → templates/user/roles.html
    }

    /** GET /user/api/roles/{roleNumber} — Single role detail */
    @GetMapping("/roles/{roleNumber}")
    public String getRoleById(@PathVariable Integer roleNumber, Model model) {
        addBreadcrumb(model, "Role Detail");
        model.addAttribute("apiEndpoint", "GET /api/v1/roles/" + roleNumber);
        model.addAttribute("apiDescription",
            "Fetches a single role by its numeric ID. " +
            "Returns roleNumber and permRole name. " +
            "Throws 404 if the role does not exist.");
        try {
            PermRoleResponseDTO role = backendApiService.getRoleById(roleNumber);
            model.addAttribute("role", role);
            model.addAttribute("roleNumber", roleNumber);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/role-detail"; // → templates/user/role-detail.html
    }

    /** GET /user/api/roles/{roleNumber}/user-count */
    @GetMapping("/roles/{roleNumber}/user-count")
    public String getRoleUserCount(@PathVariable Integer roleNumber, Model model) {
        addBreadcrumb(model, "Role User Count");
        model.addAttribute("apiEndpoint", "GET /api/v1/roles/" + roleNumber + "/user-count");
        model.addAttribute("apiDescription",
            "Returns how many registered users belong to a specific role. " +
            "Useful for system statistics and role distribution analysis. " +
            "Public endpoint — no login required. Uses countByRole_RoleNumber() repository method.");
        try {
            PermRoleResponseDTO role      = backendApiService.getRoleById(roleNumber);
            Map<String, Object> countData = backendApiService.getRoleUserCount(roleNumber);
            model.addAttribute("role",      role);
            model.addAttribute("countData", countData);
            model.addAttribute("roleNumber", roleNumber);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/role-user-count"; // → templates/user/role-user-count.html
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 3 — LOGGED-IN USER (SELF-SERVICE)
    // ═══════════════════════════════════════════════════════════

    /** GET /user/api/profile */
    @GetMapping("/profile")
    public String getMyProfile(HttpSession session, Model model) {
        addBreadcrumb(model, "My Profile");
        model.addAttribute("apiEndpoint", "GET /api/v1/user/profile");
        model.addAttribute("apiDescription",
            "Retrieves the profile of the currently logged-in user. " +
            "Extracts userId from the JWT token in the Authorization header. " +
            "Returns firstName, lastName, userName, phoneNumber, and assigned role. " +
            "Each user can only see their own profile — not other users.");
        try {
            String token = getToken(session);
            UserResponseDTO profile = backendApiService.getMyProfile(token);
            model.addAttribute("profile", profile);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/profile"; // → templates/user/profile.html
    }

    /** GET /user/api/dashboard — Aggregated dashboard */
    @GetMapping("/dashboard")
    public String getMyDashboard(HttpSession session, Model model) {
        addBreadcrumb(model, "My Dashboard");
        model.addAttribute("apiEndpoint", "GET /api/v1/user/dashboard");
        model.addAttribute("apiDescription",
            "A single aggregated API call that returns profile info + total purchase count + " +
            "all purchased inventory IDs in ONE response. " +
            "Reduces the need for 3 separate API calls. " +
            "Demonstrates API composition and efficient data delivery.");
        try {
            String token = getToken(session);
            UserDashboardDTO dashboard = backendApiService.getMyDashboard(token);
            model.addAttribute("dashboard", dashboard);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/dashboard"; // → templates/user/dashboard.html
    }

    /** GET /user/api/purchases */
    @GetMapping("/purchases")
    public String getMyPurchases(HttpSession session, Model model) {
        addBreadcrumb(model, "My Purchases");
        model.addAttribute("apiEndpoint", "GET /api/v1/user/purchases");
        model.addAttribute("apiDescription",
            "Returns the complete purchase history of the logged-in user. " +
            "Each record shows userId, inventoryId, and the buyer's name. " +
            "Uses JWT to identify the user — users cannot see each other's purchases. " +
            "The purchaselog table uses a composite primary key (userId + inventoryId).");
        try {
            String token = getToken(session);
            List<PurchaseLogResponseDTO> purchases = backendApiService.getMyPurchases(token);
            model.addAttribute("purchases", purchases);
            model.addAttribute("purchaseCount", purchases.size());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/purchases"; // → templates/user/purchases.html
    }

    /** GET /user/api/purchases/count */
    @GetMapping("/purchases/count")
    public String getMyPurchaseCount(HttpSession session, Model model) {
        addBreadcrumb(model, "Purchase Count");
        model.addAttribute("apiEndpoint", "GET /api/v1/user/purchases/count");
        model.addAttribute("apiDescription",
            "Returns the total number of inventory items purchased by the logged-in user. " +
            "Uses countById_UserId() repository method for a direct DB count query. " +
            "More efficient than loading all purchases and counting them in memory.");
        try {
            String token = getToken(session);
            Long count = backendApiService.getMyPurchaseCount(token);
            model.addAttribute("count", count);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/purchase-count"; // → templates/user/purchase-count.html
    }

    /** GET /user/api/purchase-check — Check if purchased (uses inventoryId=1 as demo) */
    @GetMapping("/purchase-check")
    public String checkPurchase(
            @RequestParam(defaultValue = "1") Integer inventoryId,
            HttpSession session, Model model) {
        addBreadcrumb(model, "Purchase Check");
        model.addAttribute("apiEndpoint", "GET /api/v1/user/purchases/check/" + inventoryId);
        model.addAttribute("apiDescription",
            "Checks whether the logged-in user has already purchased a specific " +
            "inventory item (identified by inventoryId). " +
            "Returns true or false. Prevents duplicate purchases. " +
            "Uses existsById_UserIdAndId_InventoryId() for a single DB existence check.");
        try {
            String token = getToken(session);
            Boolean result = backendApiService.checkMyPurchase(token, inventoryId);
            model.addAttribute("hasPurchased", result);
            model.addAttribute("inventoryId", inventoryId);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/purchase-check"; // → templates/user/purchase-check.html
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 4 — STORE OWNER
    // ═══════════════════════════════════════════════════════════

    /** GET /user/api/store-owner/purchases */
    @GetMapping("/store-owner/purchases")
    public String getAllPurchases(HttpSession session, Model model) {
        addBreadcrumb(model, "All Purchases");
        model.addAttribute("apiEndpoint", "GET /api/v1/store-owner/purchases");
        model.addAttribute("apiDescription",
            "Fetches all purchases across all users in the entire system. " +
            "Accessible only to StoreOwner and Admin roles. " +
            "Useful for sales reports and inventory tracking. " +
            "Returns buyer info alongside each purchase record.");
        try {
            String token = getToken(session);
            List<PurchaseLogResponseDTO> purchases = backendApiService.getAllPurchases(token);
            model.addAttribute("purchases",     purchases);
            model.addAttribute("purchaseCount", purchases.size());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/store-owner-purchases"; // → templates/user/store-owner-purchases.html
    }

    /** GET /user/api/store-owner/purchases/user?userId=X */
    @GetMapping("/store-owner/purchases/user")
    public String getPurchasesByUser(
            @RequestParam(defaultValue = "1") Integer userId,
            HttpSession session, Model model) {
        addBreadcrumb(model, "Purchases by User");
        model.addAttribute("apiEndpoint", "GET /api/v1/store-owner/purchases/user/" + userId);
        model.addAttribute("apiDescription",
            "Returns all purchases made by a specific user, identified by userId. " +
            "Store owners use this to look up individual customer purchase history. " +
            "Requires StoreOwner or Admin role.");
        try {
            String token = getToken(session);
            List<PurchaseLogResponseDTO> purchases = backendApiService.getPurchasesByUser(token, userId);
            model.addAttribute("purchases", purchases);
            model.addAttribute("userId",    userId);
        } catch (Exception e) {
            model.addAttribute("error",  e.getMessage());
            model.addAttribute("userId", userId);
        }
        return "user/store-owner-purchases"; // reuses same template
    }

    /** GET /user/api/store-owner/purchases/inventory?inventoryId=X */
    @GetMapping("/store-owner/purchases/inventory")
    public String getPurchasesByInventory(
            @RequestParam(defaultValue = "1") Integer inventoryId,
            HttpSession session, Model model) {
        addBreadcrumb(model, "Purchases by Inventory");
        model.addAttribute("apiEndpoint",
            "GET /api/v1/store-owner/purchases/inventory/" + inventoryId);
        model.addAttribute("apiDescription",
            "Returns a list of all users who purchased a specific inventory item. " +
            "Useful to see which customers own a particular book. " +
            "Requires StoreOwner or Admin role.");
        try {
            String token = getToken(session);
            List<PurchaseLogResponseDTO> purchases =
                backendApiService.getPurchasesByInventory(token, inventoryId);
            model.addAttribute("purchases",   purchases);
            model.addAttribute("inventoryId", inventoryId);
        } catch (Exception e) {
            model.addAttribute("error",       e.getMessage());
            model.addAttribute("inventoryId", inventoryId);
        }
        return "user/store-owner-purchases";
    }

    /** GET /user/api/store-owner/stats */
    @GetMapping("/store-owner/stats")
    public String getPurchaseStats(HttpSession session, Model model) {
        addBreadcrumb(model, "Purchase Statistics");
        model.addAttribute("apiEndpoint", "GET /api/v1/store-owner/purchases/stats");
        model.addAttribute("apiDescription",
            "Business intelligence endpoint returning three key metrics in one call: " +
            "total number of purchases (all records), unique buyers count " +
            "(distinct userIds who purchased), and unique items sold count " +
            "(distinct inventoryIds). Uses JPQL COUNT(DISTINCT ...) queries for efficiency.");
        try {
            String token = getToken(session);
            Map<String, Object> stats = backendApiService.getPurchaseStats(token);
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/purchase-stats"; // → templates/user/purchase-stats.html
    }

    /** GET /user/api/store-owner/top-buyers */
    @GetMapping("/store-owner/top-buyers")
    public String getTopBuyers(HttpSession session, Model model) {
        addBreadcrumb(model, "Top Buyers");
        model.addAttribute("apiEndpoint",
            "GET /api/v1/store-owner/purchases/top-buyers?limit=5");
        model.addAttribute("apiDescription",
            "Returns the top 5 users ranked by number of purchases (descending). " +
            "Uses JPQL GROUP BY with ORDER BY count to rank buyers efficiently in SQL. " +
            "Configurable limit via query param. Joins with User table to return full name alongside count.");
        try {
            String token = getToken(session);
            List<Map<String, Object>> topBuyers = backendApiService.getTopBuyers(token, 5);
            model.addAttribute("topBuyers", topBuyers);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/top-buyers"; // → templates/user/top-buyers.html
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 5 — ADMIN
    // ═══════════════════════════════════════════════════════════

    /** GET /user/api/admin/users */
    @GetMapping("/admin/users")
    public String getAllUsers(HttpSession session, Model model) {
        addBreadcrumb(model, "All Users");
        model.addAttribute("apiEndpoint", "GET /api/v1/admin/users");
        model.addAttribute("apiDescription",
            "Fetches every registered user in the system with their assigned role. " +
            "Uses a JPQL JOIN FETCH to load users and roles in one query (avoids N+1). " +
            "Accessible only to Admin role. Useful for user management and audit.");
        try {
            String token = getToken(session);
            List<UserResponseDTO> users = backendApiService.getAllUsers(token);
            model.addAttribute("users",     users);
            model.addAttribute("userCount", users.size());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/admin-users"; // → templates/user/admin-users.html
    }

    /** GET /user/api/admin/users/{userId} */
    @GetMapping("/admin/users/{userId}")
    public String getUserById(
            @PathVariable Integer userId,
            HttpSession session, Model model) {
        addBreadcrumb(model, "User Detail");
        model.addAttribute("apiEndpoint", "GET /api/v1/admin/users/" + userId);
        model.addAttribute("apiDescription",
            "Fetches full details of any user by their userId. " +
            "Admin-only — a regular user cannot look up another user's details. " +
            "Returns profile fields and the user's current role assignment.");
        try {
            String token = getToken(session);
            UserResponseDTO user = backendApiService.getUserById(token, userId);
            model.addAttribute("user",   user);
            model.addAttribute("userId", userId);
        } catch (Exception e) {
            model.addAttribute("error",  e.getMessage());
            model.addAttribute("userId", userId);
        }
        return "user/admin-user-detail"; // → templates/user/admin-user-detail.html
    }

    /** GET /user/api/admin/search?firstName=X&lastName=Y */
    @GetMapping("/admin/search")
    public String searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            HttpSession session, Model model) {
        addBreadcrumb(model, "Search Users");
        model.addAttribute("apiEndpoint",
            "GET /api/v1/admin/users/search?firstName=" + (firstName != null ? firstName : "") +
            "&lastName=" + (lastName != null ? lastName : ""));
        model.addAttribute("apiDescription",
            "Searches users by firstName or lastName (or both). " +
            "Case-insensitive matching via findByFirstNameIgnoreCase() and findByLastNameIgnoreCase() " +
            "repository methods. Deduplicates results if a user matches both params. " +
            "Admin only. Returns empty list if no matches found.");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName",  lastName);

        // Only search if at least one param is provided
        if ((firstName != null && !firstName.isBlank()) ||
            (lastName  != null && !lastName.isBlank())) {
            try {
                String token = getToken(session);
                List<UserResponseDTO> results =
                    backendApiService.searchUsers(token, firstName, lastName);
                model.addAttribute("results",     results);
                model.addAttribute("resultCount", results.size());
                model.addAttribute("searched",    true);
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "user/admin-search"; // → templates/user/admin-search.html
    }

    /** GET /user/api/admin/users/by-role?roleNumber=X */
    @GetMapping("/admin/users/by-role")
    public String getUsersByRole(
            @RequestParam(defaultValue = "2") Integer roleNumber,
            HttpSession session, Model model) {
        addBreadcrumb(model, "Users by Role");
        model.addAttribute("apiEndpoint",
            "GET /api/v1/admin/users/by-role/" + roleNumber);
        model.addAttribute("apiDescription",
            "Returns all users belonging to a specific role. " +
            "Uses findByRole_RoleNumber() Spring Data JPA method — " +
            "no custom query needed, Spring generates it automatically. " +
            "Admin only. Useful for role-based user management.");
        try {
            String token = getToken(session);
            List<PermRoleResponseDTO> allRoles = backendApiService.getAllRoles();
            List<UserResponseDTO>     users    = backendApiService.getUsersByRole(token, roleNumber);
            model.addAttribute("users",      users);
            model.addAttribute("allRoles",   allRoles);
            model.addAttribute("roleNumber", roleNumber);
        } catch (Exception e) {
            model.addAttribute("error",      e.getMessage());
            model.addAttribute("roleNumber", roleNumber);
        }
        return "user/admin-by-role"; // → templates/user/admin-by-role.html
    }

    /** GET /user/api/admin/dashboard */
    @GetMapping("/admin/dashboard")
    public String getAdminDashboard(HttpSession session, Model model) {
        addBreadcrumb(model, "Admin Dashboard");
        model.addAttribute("apiEndpoint", "GET /api/v1/admin/dashboard");
        model.addAttribute("apiDescription",
            "Master admin dashboard: returns total user count, " +
            "a breakdown of user count per role (Admin, StoreOwner, RegisteredUser, Guest), " +
            "and total purchase count — all in a single API call. " +
            "Eliminates need for multiple round trips. Admin only.");
        try {
            String token = getToken(session);
            Map<String, Object> dashboard = backendApiService.getAdminDashboard(token);
            model.addAttribute("dashboard", dashboard);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/admin-dashboard"; // → templates/user/admin-dashboard.html
    }

    // ── EDIT/FORM endpoints (update profile, change password, update role) ──

    /** GET /user/api/update-profile — Show update profile form */
    @GetMapping("/update-profile")
    public String showUpdateProfile(HttpSession session, Model model) {
        addBreadcrumb(model, "Update Profile");
        model.addAttribute("apiEndpoint", "PATCH /api/v1/user/profile");
        model.addAttribute("apiDescription",
            "Partial update of the logged-in user's profile. " +
            "Only fields provided in the request body are updated (null fields are ignored). " +
            "Validates uniqueness of new username before saving. " +
            "Returns the updated profile.");
        try {
            String token = getToken(session);
            UserResponseDTO profile = backendApiService.getMyProfile(token);
            model.addAttribute("profile", profile);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/update-profile"; // → templates/user/update-profile.html
    }

    /** GET /user/api/change-password — Show change password form */
    @GetMapping("/change-password")
    public String showChangePassword(Model model) {
        addBreadcrumb(model, "Change Password");
        model.addAttribute("apiEndpoint", "PATCH /api/v1/user/change-password");
        model.addAttribute("apiDescription",
            "Changes the logged-in user's password. " +
            "Validates: current password must match, new password must differ from current, " +
            "new password and confirm password must match. " +
            "Returns 200 on success with no data in body.");
        return "user/change-password"; // → templates/user/change-password.html
    }

    /** GET /user/api/admin/update-role — Show role update form */
    @GetMapping("/admin/update-role")
    public String showUpdateRole(HttpSession session, Model model) {
        addBreadcrumb(model, "Update User Role");
        model.addAttribute("apiEndpoint", "PATCH /api/v1/admin/users/{userId}/role");
        model.addAttribute("apiDescription",
            "Promotes or demotes any user's role. Admin only. " +
            "Takes userId in the URL path and roleNumber in the request body. " +
            "Use cases: upgrading a Guest to RegisteredUser after verification, " +
            "promoting a RegisteredUser to StoreOwner, or demoting a user.");
        try {
            String token = getToken(session);
            model.addAttribute("allUsers", backendApiService.getAllUsers(token));
            model.addAttribute("allRoles", backendApiService.getAllRoles());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/admin-update-role"; // → templates/user/admin-update-role.html
    }
}