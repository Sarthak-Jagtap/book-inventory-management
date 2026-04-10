package com.bookinventory.author.mapper;

import com.bookinventory.author.dto.BookAuthorDTO;
import com.bookinventory.author.entity.BookAuthor;
import com.bookinventory.author.entity.BookAuthorId;

public class BookAuthorMapper {

    // DTO → Entity
    public static BookAuthor toEntity(BookAuthorDTO dto) {

        BookAuthorId id = new BookAuthorId();
        id.setISBN(dto.getIsbn());
        id.setAuthorID(dto.getAuthorID());

        BookAuthor entity = new BookAuthor();
        entity.setId(id);
        entity.setPrimaryAuthor(dto.getPrimaryAuthor());

        return entity;
    }

    // Entity → DTO
    public static BookAuthorDTO toDTO(BookAuthor entity) {

        BookAuthorDTO dto = new BookAuthorDTO();

        dto.setIsbn(entity.getId().getISBN());
        dto.setAuthorID(entity.getId().getAuthorID());
        dto.setPrimaryAuthor(entity.getPrimaryAuthor());

        return dto;
    }
}
