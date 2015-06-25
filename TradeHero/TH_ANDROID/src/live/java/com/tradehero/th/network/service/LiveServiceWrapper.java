package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.LiveBrokerConstants;
import com.tradehero.th.api.live.TradingAvailableDTO;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.KYCFormFactory;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
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

    @NonNull public Observable<TradingAvailableDTO> isAvailable()
    {
        return liveServiceRx.isAvailable();
    }

    @NonNull public Observable<KYCForm> getDefaultKYCForm() // Passes null values
    {
        return isAvailable()
                .map(new Func1<TradingAvailableDTO, KYCForm>()
                {
                    @Override public KYCForm call(TradingAvailableDTO tradingAvailableDTO)
                    {
                        // TODO Make this better when the server returns a partially formed form
                        if (tradingAvailableDTO.broker == null)
                        {
                            return null;
                        }
                        else
                        {
                            switch (tradingAvailableDTO.broker.id.key)
                            {
                                case LiveBrokerConstants.AYONDO_ID:
                                    return new KYCAyondoForm();

                                default:
                                    throw new IllegalArgumentException("Unknown id: " + tradingAvailableDTO.broker.id.key);
                            }
                        }
                    }
                })
                .onErrorResumeNext(KYCFormFactory.createDefaultForm());
    }
}
