package com.bookinventory.state.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookinventory.state.entity.State;
import com.bookinventory.state.repository.StateRepository;
import com.bookinventory.user.common.exception.ResourceNotFoundException;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public State getStateByCode(String code) {
        return stateRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("State", "code", code));
    }
}