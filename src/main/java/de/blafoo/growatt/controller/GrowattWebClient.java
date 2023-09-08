package de.blafoo.growatt.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.blafoo.growatt.entity.DayResponse;
import de.blafoo.growatt.entity.EnergyRequest;
import de.blafoo.growatt.entity.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Slf4j
public class GrowattWebClient {
	
	public static final String ONE_PLANT_ID = "onePlantId";

	public static final String SELECTED_USER_ID = "selectedUserId";

	private MultiValueMap<String, String> myCookies = new LinkedMultiValueMap<>();
	
	private WebClient client;
	
	public GrowattWebClient() {
		this(HttpClient.create());
	}
	
	public GrowattWebClient(String proxyURL, int proxyPort) {
		this(HttpClient.create()
//			.followRedirect(true)
			.proxy(proxy-> proxy.type(ProxyProvider.Proxy.HTTP).host(proxyURL).port(proxyPort)));
	}
	
	private GrowattWebClient(HttpClient httpClient) {
		ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);
		  
		client = WebClient.builder()
			.baseUrl("http://server.growatt.com")
			.clientConnector(clientHttpConnector)
			.build();
	}

	public String getUserId() {
		return getCookie(SELECTED_USER_ID);
	}

	public String getPlantId() {
		return getCookie(ONE_PLANT_ID);
	}
	
	public String getCookie(String cookie) {
		return myCookies.getFirst(cookie);
	}
		
	private void writeCookies(MultiValueMap<String, String> myCookies, MultiValueMap<String, String> cookies) {
		cookies.addAll(myCookies);
	}

	private Mono<String> readCookies(MultiValueMap<String, String> myCookies, ClientResponse response) {
		MultiValueMap<String, ResponseCookie> cookies = response.cookies();
		for (var key : cookies.keySet()) {
			myCookies.add(key, cookies.getFirst(key).getValue());
			log.debug(key + " : " + cookies.getFirst(key).getValue());
		}
		return response.bodyToMono(String.class);
	}

	public String login(LoginRequest loginRequest) {
		LinkedMultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
		loginData.add("account", loginRequest.getAccount());
		loginData.add("password", loginRequest.getPassword());
		
		String login = client
			.post()
			.uri("/login")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(loginData))
            .exchangeToMono(response -> readCookies(myCookies, response))
            .block();
		
		client
			.get()
			.uri("/selectPlant/getBusiness")
			.cookies(cookies -> writeCookies(myCookies, cookies))
			.exchangeToMono(response -> readCookies(myCookies, response))
	        .block();
		
		return login;
	}

	public DayResponse getInvEnergyDayChart(EnergyRequest energyRequest) {
		LinkedMultiValueMap<String, String> dayData = new LinkedMultiValueMap<>();
		dayData.add("plantId", energyRequest.getPlantId());
		dayData.add("date", energyRequest.getDate());
		
		String dayInfos = client
			.post()
			.uri("/indexbC/inv/getInvEnergyDayChart")
			.cookies(cookies -> writeCookies(myCookies, cookies))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(dayData))
            .retrieve()
            .bodyToMono(String.class)
            .block();
		
		try {
			ObjectMapper om = new ObjectMapper();
			DayResponse result = om.readValue(dayInfos, DayResponse.class);
			return result;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
