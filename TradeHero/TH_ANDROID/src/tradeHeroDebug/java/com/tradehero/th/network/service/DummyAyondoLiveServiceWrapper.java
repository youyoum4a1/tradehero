package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.kyc.AnnualIncomeRange;
import com.androidth.general.api.kyc.Currency;
import com.androidth.general.api.kyc.EmploymentStatus;
import com.androidth.general.api.kyc.IdentityPromptInfoDTO;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.KYCFormOptionsId;
import com.androidth.general.api.kyc.LiveAvailabilityDTO;
import com.androidth.general.api.kyc.NetWorthRange;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.StepStatusesDTO;
import com.androidth.general.api.kyc.TradingPerQuarter;
import com.androidth.general.api.kyc.ayondo.DummyAyondoData;
import com.androidth.general.api.kyc.ayondo.DummyKYCAyondoUtil;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerId;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.market.Country;
import com.androidth.general.models.fastfill.Gender;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ResidenceScannedDocumentType;
import com.androidth.general.network.service.ayondo.LiveServiceAyondoRx;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.persistence.prefs.PhoneNumberVerifiedPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class DummyAyondoLiveServiceWrapper extends LiveServiceWrapper
{
    public static final int AYONDO_LIVE_BROKER_ID = 1;
    private static final String AYONDO_LIVE_BROKER_NAME = "ayondo markets";
    private static final int AYONDO_MINIMUM_AGE = 21;

    @Inject public DummyAyondoLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveServiceAyondoRx liveServiceAyondoRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull PhoneNumberVerifiedPreference phoneNumberVerifiedPreference)
    {
        super(liveServiceRx, liveServiceAyondoRx, liveBrokerSituationPreference, phoneNumberVerifiedPreference);
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        //Override with Ayondo availability
        return getAvailability()
                .map(new Func1<LiveAvailabilityDTO, LiveTradingSituationDTO>()
                {
                    @Override public LiveTradingSituationDTO call(LiveAvailabilityDTO liveAvailabilityDTO)
                    {

                        //Fake with specific ayondo form and id
                        //Needs to be removed when we support multiple brokers

                        KYCAyondoForm kycAyondoForm = new KYCAyondoForm();
                        kycAyondoForm.setCountry(liveAvailabilityDTO.getRequestorCountry());
                        kycAyondoForm.setStepStatuses(DummyKYCAyondoUtil.getSteps(kycAyondoForm).stepStatuses);
                        LiveBrokerSituationDTO brokerSituationDTO =
                                new LiveBrokerSituationDTO(new LiveBrokerDTO(new LiveBrokerId(AYONDO_LIVE_BROKER_ID), AYONDO_LIVE_BROKER_NAME),
                                        kycAyondoForm);
                        return new LiveTradingSituationDTO(Collections.singletonList(brokerSituationDTO));
                    }
                });
    }

    @NonNull @Override public Observable<StepStatusesDTO> applyToLiveBroker(@NonNull LiveBrokerId brokerId, @NonNull final KYCForm kycForm)
    {
        //Override with Ayondo
        if (kycForm instanceof KYCAyondoForm)
        {
            return Observable.just(DummyKYCAyondoUtil.getSteps((KYCAyondoForm) kycForm));
        }
        StepStatusesDTO stepStatusesDTO = new StepStatusesDTO(
                Arrays.asList(StepStatus.UNSTARTED, StepStatus.UNSTARTED, StepStatus.UNSTARTED, StepStatus.UNSTARTED,
                        StepStatus.UNSTARTED));
        return Observable.just(stepStatusesDTO);
    }

    @NonNull @Override public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull final KYCFormOptionsId optionsId)
    {
        //Override with "faked" Ayondo options
        List<Country> nationalities = new ArrayList<>(Arrays.asList(Country.values()));
        nationalities.removeAll(createNoBusinessNationalities());
        KYCFormOptionsDTO options = new KYCAyondoFormOptionsDTO(
                Arrays.asList(Gender.values()),
                Arrays.asList(Country.SG, Country.AU, Country.NZ),
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
                Arrays.asList(Currency.values()));
        return Observable.just(options);
    }

    @NonNull @Override public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(@NonNull Country country)
    {
        IdentityPromptInfoDTO identityPromptInfo;
        if (country.equals(Country.SG))
        {
            identityPromptInfo = new IdentityPromptInfoDTO(
                    country,
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-sg.png",
                    "Singapore NRIC");
        }
        else
        {
            return Observable.just(null);
        }
        return Observable.just(identityPromptInfo);
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
