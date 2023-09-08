package de.blafoo.growatt.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.blafoo.growatt.entity.DayResponse;
import de.blafoo.growatt.entity.EnergyRequest;
import de.blafoo.growatt.entity.LoginRequest;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties") 
class GrowattWebClientTest {
	
	@Value("${growatt.account}")
	private String account;
	
	@Value("${growatt.password}")
	private String password;
	
	@Value("${proxy.url}")
	private String proxyUrl;
	
	@Value("${proxy.port}")
	private String proxyPort;
	
	@Disabled // enable after setting account/password in application.properties
	@Test
	void testGrowattWebClient() {
		
		assertFalse(StringUtils.isBlank(account)); // define an user name in the application.properties
		assertFalse(StringUtils.isBlank(password)); // define a password in the application.properties
		
		GrowattWebClient client = StringUtils.isBlank(proxyUrl) ? new GrowattWebClient() : new GrowattWebClient(proxyUrl, Integer.valueOf(proxyPort));
		
		String login = client.login(new LoginRequest(account, password));
		
		assertEquals("{\"result\":1}", login);
		assertNotNull(client.getUserId());
		assertNotNull(client.getPlantId());
		
		DayResponse result = client.getInvEnergyDayChart(new EnergyRequest(client.getPlantId(), "2023-05-31"));
		
		assertEquals(1, result.getResult());
		assertEquals(24*12, result.getObj().getPac().size());
	}

}
