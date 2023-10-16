package de.blafoo.growatt.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthResponse {
	
	/** Status of query: 1 == ok */
	private Long result;
	
	private Obj obj;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public class Obj {
		
		/** Power production for each day of the month */
		private List<Double> energy;

	}
	
}
