package de.blafoo.growatt.entity;

import de.blafoo.growatt.md5.MD5;
import lombok.Getter;

@Getter
public class LoginRequest {
	
	private String account;

	private String passwordCrc;
	
	public LoginRequest(String account, String password) {
		this.account = account;
		this.passwordCrc = MD5.md5(password);
	}

}
