package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
import com.tradehero.th.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.ayondo.UsernameValidationResultDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.persistence.prefs.PhoneNumberVerifiedPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;
    @NonNull private final LiveBrokerSituationPreference liveBrokerSituationPreference;
    @NonNull private final PhoneNumberVerifiedPreference phoneNumberVerifiedPreference;

    @Inject public LiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull PhoneNumberVerifiedPreference phoneNumberVerifiedPreference)
    {
        this.liveServiceRx = liveServiceRx;
        this.liveBrokerSituationPreference = liveBrokerSituationPreference;
        this.phoneNumberVerifiedPreference = phoneNumberVerifiedPreference;
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
                })
                .startWith(liveBrokerSituationPreference.get());
    }

    @NonNull public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull KYCFormOptionsId optionsId)
    {
        return liveServiceRx.getKYCFormOptions(optionsId.brokerId.key);
    }

    @NonNull public Observable<PhoneNumberVerifiedStatusDTO> getPhoneNumberVerifiedStatus(@NonNull String phoneNumber)
    {
        return Observable.just(new PhoneNumberVerifiedStatusDTO(
                phoneNumber,
                phoneNumberVerifiedPreference.get().contains(phoneNumber)));
    }

    public Observable<UsernameValidationResultDTO> validateUserName(final String username)
    {
        return liveServiceRx.validateUserName(username)
                .map(new Func1<UsernameValidationResultDTO, UsernameValidationResultDTO>()
                {
                    @Override public UsernameValidationResultDTO call(UsernameValidationResultDTO resultDTO)
                    {
                        return new UsernameValidationResultDTO(
                                username,
                                resultDTO.isValid,
                                resultDTO.isAvailable);
                    }
                });
    }
}
