package com.bookinventory.inventory.repository;

import com.bookinventory.inventory.entity.BookCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookConditionRepository extends JpaRepository<BookCondition, Integer> {

    @Query(value = "SELECT * FROM bookcondition WHERE Ranks = ?1", nativeQuery = true)
    Optional<BookCondition> getConditionByRank(Integer rank);
}