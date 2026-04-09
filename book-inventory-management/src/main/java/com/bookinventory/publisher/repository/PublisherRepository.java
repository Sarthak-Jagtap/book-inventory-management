package com.bookinventory.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.publisher.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Integer>{

}
