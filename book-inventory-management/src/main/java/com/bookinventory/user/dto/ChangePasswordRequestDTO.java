package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequestDTO {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 4, max = 30, message = "New password must be between 4 and 30 characters")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password")
    private String confirmPassword;

    // Constructors
    public ChangePasswordRequestDTO() {}

    public ChangePasswordRequestDTO(String currentPassword,
                                    String newPassword,
                                    String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword     = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // ─── Getters & Setters ────────────────────────────────────────────
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordRequestDTO{currentPassword='[PROTECTED]', newPassword='[PROTECTED]'}";
    }
}