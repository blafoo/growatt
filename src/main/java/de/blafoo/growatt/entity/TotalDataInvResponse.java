package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TotalDataInvResponse {

	private Long result;
	private Obj obj;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public class Obj {
		
		private String epvToday;
		private String epvTotal;
		private String pac;
	}
}
