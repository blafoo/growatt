package de.blafoo.growatt.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayResponse {
	
	private Long result;
	private Obj obj;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class Obj {
		
		private List<Double> pac;

	}
	
}
