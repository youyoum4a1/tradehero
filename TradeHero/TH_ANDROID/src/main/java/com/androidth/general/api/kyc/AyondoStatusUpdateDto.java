package com.androidth.general.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by jeffgan on 14/11/16.
 */

public class AyondoStatusUpdateDto extends RealmObject{
    @JsonProperty("guid") public String guid;
    @JsonProperty("status") public String status;
    @JsonProperty("accountNumber") public String accountNumber;
    @JsonProperty("createAtUtc") public Date createAtUtc;

    public String getGuid() {
        return guid;
    }

    public String getStatus() {
        return status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Date getCreateAtUtc() {
        return createAtUtc;
    }
}
