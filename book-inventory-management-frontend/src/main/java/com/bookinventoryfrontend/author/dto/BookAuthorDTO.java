package com.bookinventoryfrontend.author.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookAuthorDTO {

    @NotBlank(message = "ISBN cannot be empty")
    @Size(min = 13, max = 13, message = "ISBN must be exactly 13 characters")
    @Pattern(regexp = "^[0-9\\-]{13}$", message = "Invalid ISBN format")
    private String isbn;

    @NotNull(message = "Author ID cannot be null")
    private Integer authorID;

    @Pattern(regexp = "^[YN]$", message = "PrimaryAuthor must be 'Y' or 'N'")
    private String primaryAuthor;
    public BookAuthorDTO() {}

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
    }

    public String getPrimaryAuthor() {
        return primaryAuthor;
    }

    public void setPrimaryAuthor(String primaryAuthor) {
        this.primaryAuthor = primaryAuthor;
    }
}
