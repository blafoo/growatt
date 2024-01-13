package de.blafoo.growatt.entity;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.Getter;

@Getter
public class EnergyRequest {
	
	/** Id of the plant that is queried. Is set as a cookie during the login process */
	private String plantId;
	
	/** e.g. 2023, 2023-06, 2023-06-19 depending on the query */
	private String date;
	
	public EnergyRequest(@NonNull String plantId) {
		this.plantId = plantId;
		this.date = null;
	}
	
	public EnergyRequest(@NonNull String plantId, @Nullable String date) {
		this.plantId = plantId;
		this.date = date;
	}

}
