package de.blafoo.growatt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        public int currPage;
        public int pages;
        public int pageSize;
        public int count;
        public int ind;
        public ArrayList<Data> datas;
        public boolean notPager;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        public String deviceType;
        public String ptoStatus;
        public String timeServer;
        public String accountName;
        public String timezone;
        public String plantId;
        public String deviceTypeName;
        public String bdcNum;
        public String nominalPower;
        public String bdcStatus;
        public Double eToday;
        public Double eMonth;
        public String datalogTypeTest;
        public Double eTotal;
        public Double pac;
        public String datalogSn;
        public String alias;
        public String location;
        public String deviceModel;
        public String sn;
        public String plantName;
        public String status;
        public String lastUpdateTime;
    }
}
