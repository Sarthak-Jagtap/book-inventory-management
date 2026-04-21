package com.bookinventoryfrontend.dto;

public class TeamMember {

	private String name;
	private String module;
	private String photoUrl;

	// ── Constructors ──────────────────────────────────────────

	public TeamMember(String name, String module) {
		this.name = name;
		this.module = module;
		this.photoUrl = null;
	}

	/** Constructor with photo */
	public TeamMember(String name, String module, String photoUrl) {
		this.name = name;
		this.module = module;
		this.photoUrl = photoUrl;
	}

	// ── Getters / Setters ─────────────────────────────────────

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}