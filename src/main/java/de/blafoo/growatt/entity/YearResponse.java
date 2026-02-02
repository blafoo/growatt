package de.blafoo.growatt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class YearResponse {

	private ResultResponse resultResponse;
	private List<Obj> obj;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Obj {

        private Datas datas;
        private String sn;
        private String type;
        private String params;
	}

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Datas {

        private List<Double> energy;
    }
	
}