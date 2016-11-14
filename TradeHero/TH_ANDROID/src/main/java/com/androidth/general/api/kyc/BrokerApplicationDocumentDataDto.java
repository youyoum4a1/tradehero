package com.androidth.general.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by jeffgan on 14/11/16.
 */

public class BrokerApplicationDocumentDataDto extends RealmObject{
    @JsonProperty("id") public int id;
    @JsonProperty("applicationId") public int applicationId;
    @JsonProperty("type") public String type;
    @JsonProperty("subtype") public String subType;
    @JsonProperty("url") public String url;
    @JsonProperty("createAtUtc") public Date createAtUtc;

    public int getId() {
        return id;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreateAtUtc() {
        return createAtUtc;
    }
}
