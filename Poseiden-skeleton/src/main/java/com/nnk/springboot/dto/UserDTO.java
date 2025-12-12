package com.nnk.springboot.dto;

import com.nnk.springboot.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {
	private Integer id;
	@NotBlank(message = "Username is mandatory")
	private String username;
	@ValidPassword
	private String password;
	@NotBlank(message = "Full Name is mandatory")
	private String fullname;
	@NotBlank(message = "Role is mandatory")
	private String role;

	public UserDTO() {}

	public UserDTO(Integer id, String username, String fullname, String role) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.role = role;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
