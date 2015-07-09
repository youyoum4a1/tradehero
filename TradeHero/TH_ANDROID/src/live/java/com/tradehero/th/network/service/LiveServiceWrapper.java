package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.IdentityPromptInfoKey;
import com.tradehero.th.api.live.KYCFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.StepStatusesDTO;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;
    @NonNull private final LiveBrokerSituationPreference liveBrokerSituationPreference;

    @Inject public LiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference)
    {
        this.liveServiceRx = liveServiceRx;
        this.liveBrokerSituationPreference = liveBrokerSituationPreference;
    }

    @NonNull public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return liveServiceRx.getLiveTradingSituation();
    }

    @NonNull public Observable<StepStatusesDTO> applyToLiveBroker(
            @NonNull LiveBrokerId brokerId,
            @NonNull KYCForm kycForm)
    {
        return liveServiceRx.applyLiveBroker(brokerId.key, kycForm);
    }

    @NonNull public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(@NonNull IdentityPromptInfoKey identityPromptInfoKey)
    {
        return liveServiceRx.getIdentityPromptInfo(identityPromptInfoKey.country.name());
    }

    @NonNull public Observable<LiveBrokerSituationDTO> getBrokerSituation()
    {
        return getLiveTradingSituation()
                .map(new Func1<LiveTradingSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(LiveTradingSituationDTO liveTradingSituation)
                    {
                        for (LiveBrokerSituationDTO situation : liveTradingSituation.brokerSituations)
                        {
                            if (situation.kycForm != null)
                            {
                                return situation;
                            }
                        }
                        throw new IllegalArgumentException("There is no available live broker situation");
                    }
                })
                .map(new Func1<LiveBrokerSituationDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(@NonNull LiveBrokerSituationDTO defaultSituation)
                    {
                        LiveBrokerSituationDTO savedSituation = liveBrokerSituationPreference.get();
                        //noinspection ConstantConditions
                        if (savedSituation.kycForm != null
                                && savedSituation.kycForm.getClass().equals(defaultSituation.kycForm.getClass()))
                        {
                            savedSituation.kycForm.pickFrom(defaultSituation.kycForm);
                        }
                        else
                        {
                            savedSituation = defaultSituation;
                        }
                        liveBrokerSituationPreference.set(savedSituation);
                        return savedSituation;
                    }
                });
    }

    @NonNull public Observable<LiveCountryDTOList> getLiveCountryList()
    {
        return liveServiceRx.getLiveCountryList();
    }

    @NonNull public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull LiveBrokerId liveBrokerId)
    {
        return liveServiceRx.getKYCFormOptions(liveBrokerId.key);
    }
}
