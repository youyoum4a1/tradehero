package com.ayondo.academyapp.api.live;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academyapp.common.persistence.DTO;
import com.ayondo.academyapp.api.kyc.KYCForm;

public class LiveBrokerSituationDTO implements DTO
{
    @NonNull public final LiveBrokerDTO broker;
    @Nullable public final KYCForm kycForm;

    public LiveBrokerSituationDTO(
            @JsonProperty("broker") @NonNull LiveBrokerDTO broker,
            @JsonProperty("kycForm") @Nullable KYCForm kycForm)
    {
        this.broker = broker;
        this.kycForm = kycForm;
    }

    @Override public int hashCode()
    {
        return broker.hashCode()
                ^ (kycForm == null ? 0 : kycForm.hashCode());
    }

    @Override public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o == this) return true;
        return o instanceof LiveBrokerSituationDTO
                && broker.equals(((LiveBrokerSituationDTO) o).broker)
                && (kycForm == null ? ((LiveBrokerSituationDTO) o).kycForm == null : kycForm.equals(((LiveBrokerSituationDTO) o).kycForm));
    }

    @Override public String toString()
    {
        return "LiveBrokerSituationDTO{" +
                "broker=" + broker +
                ", kycForm=" + kycForm +
                '}';
    }
}
