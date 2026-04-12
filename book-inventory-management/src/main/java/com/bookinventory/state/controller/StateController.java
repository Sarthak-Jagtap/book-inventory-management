package com.bookinventory.state.controller;

import org.springframework.web.bind.annotation.RestController;

import com.bookinventory.state.entity.State;
import com.bookinventory.state.service.StateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class StateController {

    @Autowired
    private StateService stateService;

    @GetMapping("/states")
    public List<State> getAllStates() {
        return stateService.getAllStates();
    }

    @GetMapping("/states/{code}")
    public State getState(@PathVariable String code) {
        return stateService.getStateByCode(code);
    }
}
