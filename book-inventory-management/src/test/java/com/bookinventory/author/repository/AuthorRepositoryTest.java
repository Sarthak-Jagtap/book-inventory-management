package com.bookinventory.author.repository;

import com.bookinventory.author.entity.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testFindByFirstNameOrLastNameContainingIgnoreCase() {

    	Author a1 = new Author();
    	a1.setAuthorID(1001);
    	a1.setFirstName("James");
    	a1.setLastName("Gosling");

    	Author a2 = new Author();
    	a2.setAuthorID(1002);
    	a2.setFirstName("Dan");
    	a2.setLastName("Kennedy");

    	List<Author> result =
    		    authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("jam","jam");

    		assertThat(result).isNotEmpty();
    		assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }
}