package com.bookinventory.author.mapper;

import com.bookinventory.author.dto.AuthorDTO;
import com.bookinventory.author.entity.Author;

public class AuthorMapper {

    public static Author toEntity(AuthorDTO dto) {
        return new Author(
                dto.getAuthorID(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhoto()
        );
    }

    public static AuthorDTO toDTO(Author author) {
        return new AuthorDTO(
                author.getAuthorID(),
                author.getFirstName(),
                author.getLastName(),
                author.getPhoto()
        );
    }
}
