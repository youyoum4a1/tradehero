package com.tradehero.th.network.service;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.IdentityPromptInfoKey;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.StepStatusesDTO;
import com.tradehero.th.persistence.prefs.KYCFormPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;
    @NonNull private final KYCFormPreference kycFormPreference;

    @Inject public LiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull KYCFormPreference kycFormPreference)
    {
        this.liveServiceRx = liveServiceRx;
        this.kycFormPreference = kycFormPreference;
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

    @NonNull public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(IdentityPromptInfoKey identityPromptInfoKey)
    {
        return liveServiceRx.getIdentityPromptInfo();
    }

    @NonNull public Observable<KYCForm> getFormToUse(@NonNull Activity activity)
    {
        return getLiveTradingSituation()
                .map(new Func1<LiveTradingSituationDTO, KYCForm>()
                {
                    @Override public KYCForm call(LiveTradingSituationDTO liveTradingSituation)
                    {
                        for (LiveBrokerSituationDTO situation : liveTradingSituation.brokerSituations)
                        {
                            if (situation.kycForm != null)
                            {
                                return situation.kycForm;
                            }
                        }
                        throw new IllegalArgumentException("There is no available kycForm");
                    }
                })
                .map(new Func1<KYCForm, KYCForm>()
                {
                    @Override public KYCForm call(@NonNull KYCForm defaultForm)
                    {
                        KYCForm savedForm = kycFormPreference.get();
                        if (savedForm.getClass().equals(defaultForm.getClass()))
                        {
                            savedForm.pickFrom(defaultForm);
                            return savedForm;
                        }
                        else
                        {
                            return defaultForm;
                        }
                    }
                });
    }
}
