package de.blafoo.growatt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TotalDataResponse {

	/** Status of query: 1 == ok */
	private Long result;
	
	private Obj obj;

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public class Obj {
		
		@JsonProperty("eRadiant") 
	    private String eRadiant;
		
	    private String pr;
	    
	    @JsonProperty("mTotal") 
	    private String mTotal;
	    
	    /** Account */
	    private String accountName;
	    
	    private String faultNum;
	    
	    @JsonProperty("mToday") 
	    private String mToday;
	    
	    private Double co2;
	    private String onlineNum;
	    
	    private String deviceNum;
	    
	    /** Power production this month */
	    @JsonProperty("eMonth") 
	    private Double eMonth;
	    
	    /** Current power production */
	    private Double pac;
	    
	    @JsonProperty("mUnitText") 
	    private String mUnitText;
	    
	    private String lostNum;
	    
	    private String waitNum;
	    
	    private String solvedNum;
	    
	    private Boolean isEnv;
	    
	    private String followUpNum;
	    
	    private String omTotal;
	    
	    @JsonProperty("mMonth") 
	    private Double mMonth;
	    
	    private String tree;
	    
	    private String plantId;
	    
	    private String etodayRadiation;
	    
	    private String plantImgName;
	    
	    private String processeingNum;
	    
	    /** PV Capacity */
	    private String nominalPower;
	    
	    /** Power production today */
	    @JsonProperty("eToday") 
	    private Double eToday;
	    
	    /** Overall power production */
	    @JsonProperty("eTotal") 
	    private Double eTotal;
	    
	    private Double coa;
	    
	    /** Installation date */
	    private String gridDate;
	    
	    /** Operation Days */
	    private Long runDay;
	}
}
