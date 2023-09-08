package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalDataResponse {

	private Long result;
	private Obj obj;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class Obj {
		
		private String epvToday;
		private String epvTotal;
		private String pac;

	}
}
