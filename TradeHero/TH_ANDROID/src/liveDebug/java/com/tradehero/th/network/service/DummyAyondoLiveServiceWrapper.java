package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.Currency;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
import com.tradehero.th.api.kyc.LiveAvailabilityDTO;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.kyc.ayondo.DummyAyondoData;
import com.tradehero.th.api.kyc.ayondo.DummyKYCAyondoUtil;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ResidenceScannedDocumentType;
import com.tradehero.th.network.service.ayondo.LiveServiceAyondoRx;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.persistence.prefs.PhoneNumberVerifiedPreference;
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
