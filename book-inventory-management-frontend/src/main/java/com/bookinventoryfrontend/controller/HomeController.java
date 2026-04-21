package com.bookinventoryfrontend.controller;

import com.bookinventoryfrontend.dto.TeamMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

	/**
	 * Root URL — redirect to /home
	 */
	@GetMapping("/")
	public String root() {
		return "redirect:/home";
	}

	/**
	 * GET /home Shows the team member grid page.
	 *
	 * IMPORTANT: Update the 5 names below with your actual team member names. The
	 * first entry is YOUR name (User Module). The rest are your teammates' names.
	 */
	@GetMapping("/home")
	public String getHomePage(Model model) {
	 
	    // ── OPTION A: With team photos ────────────────────────────
	    // Replace file names with your actual photo filenames
	    List<TeamMember> members = List.of(
	        new TeamMember("Krishna Varma",   "User Module",      "/images/team/krishna.jpg"),
	        new TeamMember("Sarthak Jagtap",  "Book Module",      "/images/team/sarthak.jpeg"),
	        new TeamMember("Yashomati",       "Inventory Module", "/images/team/yashomati.jpeg"),
	        new TeamMember("Prajwal",         "Review Module",    "/images/team/prajwal.jpeg"),
	        new TeamMember("Sudhanshu",       "Cart Module",      "/images/team/sudhanshu.jpeg")
	    );
	 
	    model.addAttribute("members", members);
	    return "home";
	}

	/**
	 * GET /team/user-module YOUR endpoint showcase page. Shows all 24 of your API
	 * endpoints organized by group.
	 */
	@GetMapping("/team/user-module")
	public String getUserModulePage(Model model) {
	    // ── Your name as it should appear at top of user-module page ──
	    model.addAttribute("memberName",  "Krishna Varma");
	    model.addAttribute("memberPhoto", "/images/team/krishna.jpg");  // ← ADD THIS
	    // If no photo, set to null:
	    // model.addAttribute("memberPhoto", null);
	    return "team/user-module";
	}

	/**
	 * GET /access-denied Shown when a user tries to access a page their role
	 * doesn't allow.
	 */
	@GetMapping("/access-denied")
	public String accessDenied() {
		return "access-denied"; // → templates/access-denied.html
	}

	// ── Placeholder pages for other team members ────────────────
	// Your teammates will replace these with their own implementations

	@GetMapping("/team/book-module")
	public String bookModule(Model model) {
		model.addAttribute("message", "Book Module — Coming Soon");
		return "redirect:/home"; // Replace with actual page when teammate builds it
	}

	@GetMapping("/team/inventory-module")
	public String inventoryModule() {
		return "redirect:/home";
	}

	@GetMapping("/team/review-module")
	public String reviewModule() {
		return "redirect:/home";
	}

	@GetMapping("/team/cart-module")
	public String cartModule() {
		return "redirect:/home";
	}
}
