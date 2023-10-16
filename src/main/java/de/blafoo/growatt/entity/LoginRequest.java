package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
	
	private String account;

	private String password;

}
