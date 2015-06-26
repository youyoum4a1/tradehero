package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.StepStatusesDTO;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import java.util.Collections;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;

    @Inject public LiveServiceWrapper(@NonNull LiveServiceRx liveServiceRx)
    {
        this.liveServiceRx = liveServiceRx;
    }

    @NonNull public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return liveServiceRx.getLiveTradingSituation()
                .onErrorReturn(new Func1<Throwable, LiveTradingSituationDTO>()
                {
                    @Override public LiveTradingSituationDTO call(Throwable throwable)
                    {
                        // TODO remove this HACK
                        LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "Ayondo");
                        KYCForm form = new KYCAyondoForm();
                        LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);
                        return new LiveTradingSituationDTO(Collections.singletonList(fakeSituation));
                    }
                });
    }

    @NonNull public Observable<StepStatusesDTO> applyToLiveBroker(
            @NonNull LiveBrokerId brokerId,
            @NonNull KYCForm kycForm)
    {
        return liveServiceRx.applyLiveBroker(brokerId.key, kycForm);
    }
}
