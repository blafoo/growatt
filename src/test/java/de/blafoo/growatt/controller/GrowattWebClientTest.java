package de.blafoo.growatt.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.blafoo.growatt.entity.DayResponse;
import de.blafoo.growatt.entity.MonthResponse;
import de.blafoo.growatt.entity.TotalDataResponse;
import de.blafoo.growatt.entity.YearResponse;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
class GrowattWebClientTest {

    @Value("${growatt.manager}")
    private String manager;

	@Value("${growatt.user}")
	private String user;
	
	@Value("${growatt.password}")
	private String password;
	
	@Value("${proxy.url}")
	private String proxyUrl;
	
	@Value("${proxy.port}")
	private String proxyPort;
	
	@Disabled // enable after setting manager/account/password in src/test/resources/application.properties
	@Test
	void testGrowattWebClient() {

        assertFalse(StringUtils.isBlank(manager)); // define a manager in the application.properties
		assertFalse(StringUtils.isBlank(user)); // define an user name in the application.properties
		assertFalse(StringUtils.isBlank(password)); // define a password in the application.properties
		
		GrowattWebClient client = StringUtils.isBlank(proxyUrl) ? new GrowattWebClient() : new GrowattWebClient(proxyUrl, Integer.parseInt(proxyPort));
		
		String login = client.login(user, password);
		assertEquals("{\"result\":1}", login);
		assertNotNull(client.getPlantId());
		
		TotalDataResponse totalData = client.getTotalData(client.getPlantId());
		assertEquals(1, totalData.getResult());
		assertEquals(manager, totalData.getObj().getAccountName());
		assertEquals(client.getPlantId(), totalData.getObj().getPlantId());
		
		YearResponse years = client.getEnergyTotalChart(client.getPlantId(), "2025");
		assertTrue(years.getResult());
        assertFalse(years.getObj().isEmpty());
		
		DayResponse day = client.getEnergyDayChart(client.getPlantId(), "2025-05-31");
        assertTrue(day.getResult());
		assertEquals(24*12, day.getObj().getFirst().getDatas().getPac().size());
		
		MonthResponse month = client.getEnergyMonthChart(client.getPlantId(), "2025-05");
        assertTrue(month.getResult());
		assertEquals(31, month.getObj().getFirst().getDatas().getEnergy().size());
		
		YearResponse year = client.getEnergyYearChart(client.getPlantId(), "2025");
        assertTrue(year.getResult());
		assertEquals(12, year.getObj().getFirst().getDatas().getEnergy().size());
	}

}
