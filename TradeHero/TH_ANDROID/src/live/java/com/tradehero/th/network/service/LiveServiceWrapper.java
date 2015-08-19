package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.BrokerApplicationDTO;
import com.tradehero.th.api.kyc.BrokerDocumentUploadResponseDTO;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
import com.tradehero.th.api.kyc.LiveAvailabilityDTO;
import com.tradehero.th.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoIDCheckDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.UsernameValidationResultDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerKnowledge;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.network.service.ayondo.LiveServiceAyondoRx;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.persistence.prefs.PhoneNumberVerifiedPreference;
import com.tradehero.th.utils.GraphicUtil;
import java.io.File;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class LiveServiceWrapper
{
    @NonNull private final LiveServiceRx liveServiceRx;
    @NonNull private final LiveServiceAyondoRx liveServiceAyondoRx;
    @NonNull private final LiveBrokerSituationPreference liveBrokerSituationPreference;
    @NonNull private final PhoneNumberVerifiedPreference phoneNumberVerifiedPreference;

    @Inject public LiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveServiceAyondoRx liveServiceAyondoRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull PhoneNumberVerifiedPreference phoneNumberVerifiedPreference)
    {
        this.liveServiceRx = liveServiceRx;
        this.liveServiceAyondoRx = liveServiceAyondoRx;
        this.liveBrokerSituationPreference = liveBrokerSituationPreference;
        this.phoneNumberVerifiedPreference = phoneNumberVerifiedPreference;
    }

    @NonNull public Observable<LiveAvailabilityDTO> getAvailability()
    {
        return liveServiceAyondoRx.getAvailability()
                .cast(LiveAvailabilityDTO.class);
        // .merge() with other specific ones when time comes
    }

    @NonNull public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return liveServiceRx.getLiveTradingSituation();
    }

    @NonNull public Observable<StepStatusesDTO> applyToLiveBroker(
            @NonNull LiveBrokerId brokerId,
            @NonNull KYCForm kycForm)
    {
        if (brokerId.key.equals(LiveBrokerKnowledge.BROKER_ID_AYONDO))
        {
            return liveServiceAyondoRx.applyLiveBroker(kycForm);
        }
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
                .startWith(Observable.defer(new Func0<Observable<LiveBrokerSituationDTO>>()
                {
                    @Override public Observable<LiveBrokerSituationDTO> call()
                    {
                        return Observable.just(liveBrokerSituationPreference.get());
                    }
                }));
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

    public Observable<UsernameValidationResultDTO> validateUserName(
            @NonNull LiveBrokerId brokerId,
            @NonNull final String username)
    {
        return (brokerId.key.equals(LiveBrokerKnowledge.BROKER_ID_AYONDO)
                ? liveServiceAyondoRx.validateUserName(username)
                : liveServiceRx.validateUserName(brokerId.key, username))
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

    public void submitPhoneNumberVerifiedStatus(String formattedPhoneNumber)
    {
        phoneNumberVerifiedPreference.addVerifiedNumber(formattedPhoneNumber);
    }

    public Observable<BrokerApplicationDTO> createOrUpdateLead(KYCForm kycForm)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            //TODO change to specific class
            return liveServiceAyondoRx.createOrUpdateLead(new AyondoLeadDTO((KYCAyondoForm) kycForm));
        }
        else
        {
            //TODO when we have multiple brokers
            return Observable.just(null);
        }
    }

    public Observable<BrokerDocumentUploadResponseDTO> uploadDocument(File f)
    {
        return liveServiceRx.uploadDocument(GraphicUtil.fromFile(f));
    }

    public Observable<AyondoIDCheckDTO> checkNeedIdentityDocument(AyondoLeadUserIdentityDTO ayondoLeadUserIdentityDTO)
    {
        return liveServiceAyondoRx.checkNeedIdentity(ayondoLeadUserIdentityDTO);
    }

    public Observable<AyondoAddressCheckDTO> checkNeedResidencyDocument(AyondoLeadAddressDTO ayondoLeadAddressDTO)
    {
        return liveServiceAyondoRx.checkNeedResidency(ayondoLeadAddressDTO);
    }

    public Observable<BrokerApplicationDTO> submitApplication(KYCForm kycForm)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            return liveServiceAyondoRx.submitApplication(new AyondoAccountCreationDTO((KYCAyondoForm) kycForm));
        }
        else
        {
            //TODO when we have multiple brokers
            return Observable.just(null);
        }
    }
}
