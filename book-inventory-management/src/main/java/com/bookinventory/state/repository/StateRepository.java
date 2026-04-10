package com.bookinventory.state.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookinventory.state.entity.State;

public interface StateRepository extends JpaRepository<State, String>{

}
