package com.androidth.general.network.service;

import android.support.annotation.NonNull;

import com.androidth.general.api.kyc.AnnualIncomeRange;
import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.BrokerDocumentUploadResponseDTO;
import com.androidth.general.api.kyc.Currency;
import com.androidth.general.api.kyc.EmploymentStatus;
import com.androidth.general.api.kyc.IdentityPromptInfoDTO;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.KYCFormOptionsId;
import com.androidth.general.api.kyc.LiveAvailabilityDTO;
import com.androidth.general.api.kyc.NetWorthRange;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import com.androidth.general.api.kyc.PhoneNumberVerifiedStatusDTO;
import com.androidth.general.api.kyc.StepStatusesDTO;
import com.androidth.general.api.kyc.TradingPerQuarter;
import com.androidth.general.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.androidth.general.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.androidth.general.api.kyc.ayondo.AyondoIDCheckDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
import com.androidth.general.api.kyc.ayondo.DummyAyondoData;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.kyc.ayondo.UsernameValidationResultDTO;
import com.androidth.general.api.live.LiveBrokerId;
import com.androidth.general.api.live.LiveBrokerKnowledge;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.market.Country;
import com.androidth.general.models.fastfill.Gender;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ResidenceScannedDocumentType;
import com.androidth.general.network.service.ayondo.LiveServiceAyondoRx;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.persistence.prefs.PhoneNumberVerifiedPreference;
import com.androidth.general.utils.GraphicUtil;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;


public class LiveServiceWrapper
{
    private static final int AYONDO_MINIMUM_AGE = 21;

    @NonNull private final com.androidth.general.network.service.LiveServiceRx liveServiceRx;
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

    @NonNull @RxLogObservable public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        //Generic calls for multi brokers
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

    @NonNull @RxLogObservable public Observable<LiveBrokerSituationDTO> getBrokerSituation()
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
//        return liveServiceRx.getKYCFormOptions(optionsId.brokerId.key);
        List<Country> nationalities = new ArrayList<>(Arrays.asList(Country.values()));
        nationalities.removeAll(createNoBusinessNationalities());
        KYCFormOptionsDTO options = new KYCAyondoFormOptionsDTO(
                Arrays.asList(Gender.values()),
                Arrays.asList(Country.MY, Country.SG, Country.TH, Country.ID),
                nationalities,
                Arrays.asList(Country.SG, Country.AU, Country.NZ),
                Arrays.asList(AnnualIncomeRange.values()),
                Arrays.asList(NetWorthRange.values()),
                Arrays.asList(PercentNetWorthForInvestmentRange.values()),
                Arrays.asList(EmploymentStatus.values()),
                Arrays.asList(TradingPerQuarter.values()),
                DummyAyondoData.DEFAULT_MAX_ADDRESS_REQUIRED,
                Arrays.asList(IdentityScannedDocumentType.values()),
                Arrays.asList(ResidenceScannedDocumentType.values()),
                DummyAyondoData.TERMS_CONDITIONS_URL,
                DummyAyondoData.RISK_WARNING_DISCLAIMER_URL,
                DummyAyondoData.DATA_SHARING_AGREEMENT_URL,
                AYONDO_MINIMUM_AGE,
                Arrays.asList(Currency.values()),
                Arrays.asList("Kuala Lumpur", "Labuan", "Sarawak", "Penang", "Selangor", "Malacca", "Negeri Sembilan", "Pahang", "Johor", "Terengganu", "Perak", "Sabah", "Perlis", "Kedah", "Kelantan"),
                Arrays.asList("Online", "Events"));
        return Observable.just(options);
    }

    @NonNull public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(@NonNull Country country)
    {
        return Observable.just(new IdentityPromptInfoDTO(Country.SG, "fake", "Wait until we support more countries"));
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
            return liveServiceAyondoRx
                    .createOrUpdateLead(
                            new AyondoLeadDTO((KYCAyondoForm) kycForm)
                    );
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

    public Observable<Boolean>enrollCompetition(int providerId, int userId) {
        return liveServiceRx.enrollCompetition(providerId, userId);
    }

    @NonNull public static List<Country> createNoBusinessNationalities()
    {
        return Collections.unmodifiableList(Arrays.asList(
                Country.NONE,
                Country.IR,
                Country.KP,
                Country.CU,
                Country.EC,
                Country.ET,
                Country.KE,
                Country.MM,
                Country.NG,
                Country.PK,
                Country.ST,
                Country.SY,
                Country.TZ,
                Country.TR,
                Country.VN,
                Country.YE,
                Country.BD,
                Country.IQ,
                Country.KG,
                Country.LY,
                Country.TJ,
                Country.ZW,
                Country.SD,
                Country.AF,
                Country.LA,
                Country.DZ,
                Country.AL,
                Country.AO,
                Country.AG,
                Country.AR,
                Country.KH,
                Country.KW,
                Country.MN,
                Country.NA,
                Country.SO,
                Country.US
        ));
    }
}
