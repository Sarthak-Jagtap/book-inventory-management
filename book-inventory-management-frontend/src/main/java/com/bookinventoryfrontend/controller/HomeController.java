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
	public String home(Model model) {

		// ── UPDATE THESE NAMES WITH YOUR ACTUAL TEAM ──────────────
		List<TeamMember> members = Arrays.asList(new TeamMember("Krishna Varma", // ← CHANGE THIS to your name
				"User Module", "user-module", "Auth · Users · Roles · Purchases"),
				new TeamMember("Sarthak Jagtap", // ← CHANGE THIS
						"Book Module", "book-module", "Books · Authors · Categories · Publishers"),
				new TeamMember("Yashomati", // ← CHANGE THIS
						"Inventory Module", "inventory-module", "Inventory · BookCondition · Stock"),
				new TeamMember("Prajwal", // ← CHANGE THIS
						"Review Module", "review-module", "BookReviews · Ratings · Reviewers"),
				new TeamMember("Sudhanshu", // ← CHANGE THIS
						"Cart Module", "cart-module", "ShoppingCart · Checkout · State"));
		// ──────────────────────────────────────────────────────────

		model.addAttribute("members", members);
		model.addAttribute("activePage", "home");
		return "home"; // → templates/home.html
	}

	/**
	 * GET /team/user-module YOUR endpoint showcase page. Shows all 24 of your API
	 * endpoints organized by group.
	 */
	@GetMapping("/team/user-module")
	public String userModulePage(Model model) {
		model.addAttribute("memberName", "Krishna Varma"); // ← CHANGE to your name
		model.addAttribute("activePage", "user-module");
		return "team/user-module"; // → templates/team/user-module.html
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
		return "team/book-module"; // Replace with actual page when teammate builds it
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
