package de.blafoo.growatt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.blafoo.growatt.entity.DayResponse;
import de.blafoo.growatt.entity.DevicesResponse;
import de.blafoo.growatt.entity.MonthResponse;
import de.blafoo.growatt.entity.YearResponse;
import de.blafoo.growatt.md5.MD5;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class GrowattWebClient {
	
	public static final String ONE_PLANT_ID = "onePlantId";

	public static final String PATTERN_MONTH = "yyyy-MM";
	public static final String PATTERN_DAY = "yyyy-MM-dd";

	private final MultiValueMap<String, String> cookieJar = new LinkedMultiValueMap<>();
	
	private final WebClient client;
	
	/**
	 * Constructor without a proxy
	 */
	public GrowattWebClient() {
		this(HttpClient
			.create()
			.followRedirect(true));
	}
	
	/**
	 * Constructor when behind a proxy
	 * 
	 * @param proxyURL
	 * @param proxyPort
	 */
	public GrowattWebClient(String proxyURL, int proxyPort) {
		this(HttpClient
			.create()
			.followRedirect(true)
			.proxy(proxy-> proxy.type(ProxyProvider.Proxy.HTTP).host(proxyURL).port(proxyPort)));
	}
	
	private GrowattWebClient(HttpClient httpClient) {
		ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
		  
		client = WebClient.builder()
			.baseUrl("http://server.growatt.com")
			.clientConnector(clientHttpConnector)
			.build();
	}

	/** 
	 * Get the internal plant id. Needed for following requests.
	 * @return
	 */
	public String getPlantId() {
		return getCookie(ONE_PLANT_ID);
	}
	
	private String getCookie(String cookie) {
		return cookieJar.getFirst(cookie);
	}
		
	private void writeCookies(MultiValueMap<String, String> myCookies, MultiValueMap<String, String> cookies) {
		cookies.addAll(myCookies);
	}

	private Mono<String> readCookies(MultiValueMap<String, String> myCookies, ClientResponse response) {
		MultiValueMap<String, ResponseCookie> cookies = response.cookies();
		for (var key : cookies.keySet()) {
			myCookies.add(key, cookies.getFirst(key).getValue());
            log.debug("{} : {}", key, cookies.getFirst(key).getValue());
		}
		return response.bodyToMono(String.class);
	}

	/**
	 * Login into server.growatt.com. Initialize all needed cookies for the following requests.
	 */
	public String login(String account, String password) {
		LinkedMultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
		loginData.add("account", account);
		loginData.add("passwordCrc", MD5.md5(password));
		
		return client
			.post()
			.uri("/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(loginData))
            .exchangeToMono(response -> readCookies(cookieJar, response))
            .block();
    }
	
	/**
	 * Retrieve generic informations about the power production.
	 */
	public DevicesResponse getDevicesByPlantList(String plantId) {
		LinkedMultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
		payload.add("currPage", "1");
		payload.add("plantId", plantId);
		return request("/panel/getDevicesByPlantList", payload, DevicesResponse.class);
	}

	/**
	 * Retrieve the power production for a specific day within the last 3 months. Does not work for days older than 3 months.
	 * The response contains 288 values for each five minute intervall of the day.
	 */
	public DayResponse getEnergyDayChart(String plantId, LocalDate date) {
		return request("/energy/compare/getDevicesDayChart", plantId, date.format(DateTimeFormatter.ofPattern(PATTERN_DAY)), null, "pac", DayResponse.class);
	}
	
	/**
	 * Retrieve the power production for a specific month. The day is ignored
	 * The response contains one value for each day of the month.
	 */
	public MonthResponse getEnergyMonthChart(String plantId, LocalDate date) {
		return request("/energy/compare/getDevicesMonthChart", plantId, date.format(DateTimeFormatter.ofPattern(PATTERN_MONTH)), null, "energy,autoEnergy", MonthResponse.class);
	}
	
	/**
	 * Retrieve the power production for a specific year.
	 * The response contains one value for each month of the month.
	 */
	public YearResponse getEnergyYearChart(String plantId, int year) {
		return request("/energy/compare/getDevicesYearChart", plantId, null, year, "energy,autoEnergy", YearResponse.class);
	}
	
	/**
	 * Retrieve the power production for the last 5 years.
     * The response contains a value for every year.
	 */
	public YearResponse getEnergyTotalChart(String plantId, int lastYear) {
		return request("/energy/compare/getDevicesTotalChart", plantId, null, lastYear, "energy,autoEnergy", YearResponse.class);
	}

	private <T> T request(String uri, @NonNull String plantId, @Nullable String date, @Nullable Integer year, String params, Class<T> clazz) {
		LinkedMultiValueMap<String, String> payload = createBody(plantId, date, year, params);
		return request(uri, payload, clazz);
	}

	private <T> T request(String uri, LinkedMultiValueMap<String, String> payload, Class<T> clazz) {
		String infos = client
			.post()
			.uri(uri)
			.cookies(cookies -> writeCookies(cookieJar, cookies))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(payload))
            .retrieve()
            .bodyToMono(String.class)
            .block();

		try {
			if (StringUtils.isNotBlank(infos)) {
				ObjectMapper om = new ObjectMapper();
				return om.readValue(infos, clazz);
			} else
				log.error("POST to {} returned 'null'", uri);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static @NonNull LinkedMultiValueMap<String, String> createBody(@NonNull String plantId, @Nullable String date, @Nullable Integer year, @Nullable String params) {
		LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
		data.add("plantId", plantId);
		if (StringUtils.isNotEmpty(date))
			data.add("date", date);
		if (year != null)
			data.add("year", String.valueOf(year));
		if (params != null)
			data.add("jsonData", "[{\"type\":\"plant\",\"sn\":\"%s\",\"params\":\"%s\"}]".formatted(plantId, params));
		return data;
	}
}
