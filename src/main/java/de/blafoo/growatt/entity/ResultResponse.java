package de.blafoo.growatt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private boolean result;

    public ResultResponse(int value) {
        this.result = value == 1;
    }

    public Boolean isSuccess() {
        return result;
    }
}
