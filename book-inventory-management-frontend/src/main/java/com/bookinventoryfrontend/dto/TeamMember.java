package com.bookinventoryfrontend.dto;

public class TeamMember {
	private String name;
	private String module;
	private String slug; // URL slug e.g. "user-module"
	private String description;

	public TeamMember() {
	}

	public TeamMember(String name, String module, String slug, String description) {
		this.name = name;
		this.module = module;
		this.slug = slug;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String v) {
		this.name = v;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String v) {
		this.module = v;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String v) {
		this.slug = v;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String v) {
		this.description = v;
	}

	// For avatar initials
	public String getInitials() {
		if (name == null || name.isBlank())
			return "??";
		String[] parts = name.trim().split("\\s+");
		if (parts.length >= 2)
			return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
		return name.substring(0, Math.min(2, name.length())).toUpperCase();
	}
}
