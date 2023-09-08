package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnergyRequest {
	
	private String plantId;
	
	/** e.g. 2023, 2023-06, 2023-06-19 */
	private String date;

}
