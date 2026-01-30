package de.blafoo.growatt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DevicesResponse {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Boolean result;
    private Obj obj;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Obj{
        private int currPage;
        private int pages;
        private int pageSize;
        private int count;
        private int ind;
        private ArrayList<Data> datas;
        private boolean notPager;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String deviceType;
        private String ptoStatus;
        private String timeServer;
        private String accountName;
        private String timezone;
        private String plantId;
        private String deviceTypeName;
        private String bdcNum;
        private String nominalPower;
        private String bdcStatus;
        @JsonProperty("eToday")
        private Double eToday;
        @JsonProperty("eMonth")
        private Double eMonth;
        private String datalogTypeTest;
        @JsonProperty("eTotal")
        private Double eTotal;
        @JsonProperty("pac")
        private Double pac;
        private String datalogSn;
        private String alias;
        private String location;
        private String deviceModel;
        private String sn;
        private String plantName;
        private String status;
        private String lastUpdateTime;
    }
}
