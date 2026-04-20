package com.bookinventoryfrontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookinventoryfrontend.dto.PublisherRequestDTO;
import com.bookinventoryfrontend.service.PublisherService;

@Controller
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    // GET All Publisher
    @GetMapping("/publishers")
    public String getAll(Model model) {
        model.addAttribute("publishers", publisherService.getAllPublishers());
        return "publisher/list";
    }

    // GET BY ID
    @GetMapping("/publishers/id")
    public String idForm() {
        return "publisher/id-form";
    }

    @GetMapping("/publishers/details")
    public String getById(@RequestParam Integer publisherId, Model model) {
        model.addAttribute("publisher",
                publisherService.getPublisherById(publisherId));
        return "publisher/details";
    }

    // GET Publishers by State Code Filter
    @GetMapping("/publishers/state")
    public String getByState(
            @RequestParam(required = false) String stateCode,
            Model model) {

        if (stateCode != null && !stateCode.isEmpty()) {
            model.addAttribute("publishers",
                    publisherService.getPublishersByState(stateCode));
        }

        return "publisher/state";
    }

    // POST Create Publisher 
    @GetMapping("/publishers/add")
    public String addForm() {
        return "publisher/add";
    }

    @PostMapping("/publishers/add")
    public String create(@ModelAttribute PublisherRequestDTO dto, Model model) {
        model.addAttribute("publisher", publisherService.createPublisher(dto));
        return "publisher/details";
    }

    // UPDATE Update Publisher
    @GetMapping("/publishers/update")
    public String updateForm() {
        return "publisher/update";
    }

    @PostMapping("/publishers/update")
    public String update(
            @RequestParam Integer publisherId,
            @ModelAttribute PublisherRequestDTO dto,
            Model model) {

        model.addAttribute("publisher",
                publisherService.updatePublisher(publisherId, dto));

        return "publisher/details";
    }

    // DELETE Delete user
    @GetMapping("/publishers/delete")
    public String deleteForm() {
        return "publisher/delete";
    }

    @PostMapping("/publishers/delete")
    public String delete(@RequestParam Integer publisherId, Model model) {
        publisherService.deletePublisher(publisherId);
        model.addAttribute("message", "Publisher deleted successfully");
        return "publisher/result";
    }
}
