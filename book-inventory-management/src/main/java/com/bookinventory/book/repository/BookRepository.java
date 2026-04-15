package com.bookinventory.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, String> {

	Page<Book> findByCategory_CatId(Integer categoryId, Pageable pageable);

	Page<Book> findByPublisher_PublisherId(Integer publisherId, Pageable pageable);

	@Query("""
			    SELECT b FROM Book b
			    WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
			    AND (:categoryId IS NULL OR b.category.catId = :categoryId)
			    AND (:publisherId IS NULL OR b.publisher.publisherId = :publisherId)
			""")
	List<Book> searchBooks(@Param("title") String title, @Param("categoryId") Integer categoryId,
			@Param("publisherId") Integer publisherId);
}
