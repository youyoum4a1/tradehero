package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.kyc.KYCForm;

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

    public boolean hasSameFields(@NonNull LiveBrokerSituationDTO other)
    {
        return broker.hasSameFields(other.broker)
                && (kycForm == null ? other.kycForm == null : (other.kycForm != null && kycForm.hasSameFields(other.kycForm)));
    }
}
