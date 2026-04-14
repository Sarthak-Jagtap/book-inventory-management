package com.bookinventory.publisher.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.publisher.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Integer>{

	
	List<Publisher> findByState_StateCode(String stateCode);
}
