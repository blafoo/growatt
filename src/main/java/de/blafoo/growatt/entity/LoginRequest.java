package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
	
	private String account;

	private String password;

}
