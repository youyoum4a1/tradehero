package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.models.kyc.KYCForm;
import javax.inject.Inject;
import rx.Observable;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;

    @Inject public LiveServiceWrapper(@NonNull LiveServiceRx liveServiceRx)
    {
        this.liveServiceRx = liveServiceRx;
    }

    @NonNull public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return liveServiceRx.getLiveTradingSituation();
    }

    @NonNull public Observable<BaseResponseDTO> applyToLiveBroker(
            @NonNull LiveBrokerId brokerId,
            @NonNull KYCForm kycForm)
    {
        return liveServiceRx.applyLiveBroker(brokerId.key, kycForm);
    }
}
