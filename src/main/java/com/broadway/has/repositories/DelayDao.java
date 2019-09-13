package com.broadway.has.repositories;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "delays")
public class DelayDao {

    @Id
    private String id;

    private String reason;
    private boolean userNotified;
    private Date userNotifiedTimestamp;
    private int valveNumber;
    private Date delayStartTimestamp;
    private Date delayEndTimestamp;
    private Date modifiedTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUserNotified() {
        return userNotified;
    }

    public void setUserNotified(boolean userNotified) {
        this.userNotified = userNotified;
    }

    public Date getUserNotifiedTimestamp() {
        return userNotifiedTimestamp;
    }

    public void setUserNotifiedTimestamp(Date userNotifiedTimestamp) {
        this.userNotifiedTimestamp = userNotifiedTimestamp;
    }

    public int getValveNumber() {
        return valveNumber;
    }

    public void setValveNumber(int valveNumber) {
        this.valveNumber = valveNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDelayStartTimestamp() {
        return delayStartTimestamp;
    }

    public void setDelayStartTimestamp(Date delayStartTimestamp) {
        this.delayStartTimestamp = delayStartTimestamp;
    }

    public Date getDelayEndTimestamp() {
        return delayEndTimestamp;
    }

    public void setDelayEndTimestamp(Date delayEndTimestamp) {
        this.delayEndTimestamp = delayEndTimestamp;
    }

    public Date getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(Date modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }
}
