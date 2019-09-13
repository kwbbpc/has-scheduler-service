package com.broadway.has.requests;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DelayRequest {

    private Date delayUntilTimestamp;
    private Integer valveNumber;
    private String reason;

    public Date getDelayUntilTimestamp() {
        return delayUntilTimestamp;
    }

    public void setDelayUntilTimestamp(Date delayUntilTimestamp) {
        this.delayUntilTimestamp = delayUntilTimestamp;
    }

    public Integer getValveNumber() {
        return valveNumber;
    }

    public void setValveNumber(Integer valveNumber) {
        this.valveNumber = valveNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
