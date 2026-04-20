package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.service.StateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping("/states")
    public String getStates(Model model) {

        model.addAttribute("states", stateService.getAllStates());

        return "state/states";
    }

    @GetMapping("/states/code")
    public String showStateForm() {
        return "state/code-form";
    }

    @GetMapping("/states/details")
    public String getStateByCode(@RequestParam String stateCode, Model model) {

        model.addAttribute("state", stateService.getStateByStatecode(stateCode));

        return "state/details";
    }
}