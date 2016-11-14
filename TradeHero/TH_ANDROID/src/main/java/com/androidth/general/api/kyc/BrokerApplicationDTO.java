package com.androidth.general.api.kyc;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class BrokerApplicationDTO extends RealmObject implements DTO
{
    @JsonProperty("applicationId") public int applicationId;
    @JsonProperty("brokerId") public int brokerId;
    @JsonProperty("userId") public int userId;
    @JsonProperty("guid") public String guid;
    @JsonProperty("applicationStatus") public String applicationStatus;
    @JsonProperty("accountNumber") public String accountNumber;
    @JsonProperty("isSubmitted") public boolean isSubmitted;
    @JsonProperty("AyondoStatusUpdates") public RealmList<AyondoStatusUpdateDto> ayondoStatusUpdates;
    @JsonProperty("BrokerApplicationDocumentDatas") public RealmList<BrokerApplicationDocumentDataDto> brokerApplicationDocumentDataDtos;

    public BrokerApplicationDTO()
    {
        super();
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public int getUserId() {
        return userId;
    }

    public String getGuid() {
        return guid;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public List<AyondoStatusUpdateDto> getAyondoStatusUpdates() {
        return ayondoStatusUpdates;
    }

    public List<BrokerApplicationDocumentDataDto> getBrokerApplicationDocumentDataDtos() {
        return brokerApplicationDocumentDataDtos;
    }

}
