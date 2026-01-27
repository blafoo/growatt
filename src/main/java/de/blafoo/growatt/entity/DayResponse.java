package de.blafoo.growatt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DayResponse {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Boolean result;
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

        /** Power production for each 5 minute interval of the day, in total 288 values */
        private List<Double> pac;
        private List<Double> energy;
    }

}
