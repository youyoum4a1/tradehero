package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.ObjectMapperWrapper;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
import com.tradehero.th.api.kyc.ayondo.DummyAyondoData;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class DummyLiveServiceWrapper extends LiveServiceWrapper
{
    private static final int TIME_OUT_SECONDS = 10;
    private final Country pretendInCountry = Country.SG;

    @NonNull private final ObjectMapperWrapper objectMapperWrapper;

    @Inject public DummyLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull ObjectMapperWrapper objectMapperWrapper)
    {
        super(liveServiceRx, liveBrokerSituationPreference);
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return super.getLiveTradingSituation()
                //.timeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .onErrorResumeNext(
                        new Func1<Throwable, Observable<? extends LiveTradingSituationDTO>>()
                        {
                            @Override public Observable<? extends LiveTradingSituationDTO> call(Throwable throwable)
                            {
                                LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "ayondo markets");
                                KYCAyondoForm form = new KYCAyondoForm();
                                form.setCountry(Country.SG);
                                LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);
                                return Observable.just(new LiveTradingSituationDTO(Collections.singletonList(fakeSituation)));
                            }
                        });
    }

    @NonNull @Override public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull KYCFormOptionsId optionsId)
    {
        return super.getKYCFormOptions(optionsId)
                .map(new Func1<KYCFormOptionsDTO, KYCFormOptionsDTO>()
                {
                    @Override public KYCFormOptionsDTO call(KYCFormOptionsDTO kycFormOptionsDTO)
                    {
                        if (kycFormOptionsDTO.getIdentityPromptInfo() != null)
                        {
                            return kycFormOptionsDTO;
                        }
                        return new KYCAyondoFormOptionsDTO(
                                createIdentityPromptInfo(),
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).allowedMobilePhoneCountries,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).allowedNationalityCountries,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).allowedResidencyCountries,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).annualIncomeOptions,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).netWorthOptions,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).percentNetWorthOptions,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).employmentStatusOptions,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).tradingPerQuarterOptions,
                                ((KYCAyondoFormOptionsDTO) kycFormOptionsDTO).minAge);
                    }
                })
                        //.timeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .onErrorResumeNext(
                        new Func1<Throwable, Observable<? extends KYCFormOptionsDTO>>()
                        {
                            @Override public Observable<? extends KYCFormOptionsDTO> call(Throwable throwable)
                            {
                                try
                                {
                                    return Observable.just(objectMapperWrapper.readValue(DummyAyondoData.KYC_OPTIONS, KYCAyondoFormOptionsDTO.class));
                                } catch (IOException e)
                                {
                                    Timber.e(e, "Failed to deserialise dummy Options");
                                }

                                List<Country> nationalities = new ArrayList<>(Arrays.asList(Country.values()));
                                nationalities.removeAll(createNoBusinessNationalities());
                                KYCFormOptionsDTO options = new KYCAyondoFormOptionsDTO(
                                        createIdentityPromptInfo(),
                                        Arrays.asList(Country.SG, Country.AU, Country.GB),
                                        nationalities,
                                        Arrays.asList(Country.SG, Country.AU, Country.GB),
                                        Arrays.asList(AnnualIncomeRange.LESS15KUSD, AnnualIncomeRange.FROM15KUSDTO40KUSD,
                                                AnnualIncomeRange.FROM40KUSDTO70KUSD, AnnualIncomeRange.FROM70KUSDTO100KUSD,
                                                AnnualIncomeRange.MORETHAN100KUSD),
                                        Arrays.asList(NetWorthRange.LESS15KUSD, NetWorthRange.FROM15KUSDTO40KUSD,
                                                NetWorthRange.FROM40KUSDTO70KUSD, NetWorthRange.FROM70KUSDTO100KUSD,
                                                NetWorthRange.FROM100KUSDTO500KUSD, NetWorthRange.MORETHAN500KUSD),
                                        Arrays.asList(PercentNetWorthForInvestmentRange.LESSTHAN25P,
                                                PercentNetWorthForInvestmentRange.FROM25PTO50P,
                                                PercentNetWorthForInvestmentRange.FROM51PTO75P,
                                                PercentNetWorthForInvestmentRange.MORETHAN75P),
                                        Arrays.asList(EmploymentStatus.EMPLOYED, EmploymentStatus.SELFEMPLOYED,
                                                EmploymentStatus.UNEMPLOYED, EmploymentStatus.RETIRED,
                                                EmploymentStatus.STUDENT),
                                        Arrays.asList(TradingPerQuarter.NONE, TradingPerQuarter.ONE_TO_FIVE, TradingPerQuarter.SIX_TO_TEN,
                                                TradingPerQuarter.OVER_TEN), 21);
                                return Observable.just(options);
                            }
                        });
    }

    @NonNull private IdentityPromptInfoDTO createIdentityPromptInfo()
    {
        IdentityPromptInfoDTO identityPromptInfo;
        if (pretendInCountry.equals(Country.AU))
        {
            identityPromptInfo = new IdentityPromptInfoDTO(
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-au.png",
                    "Do you have your Passport with you?");
        }
        else if (pretendInCountry.equals(Country.SG))
        {
            identityPromptInfo = new IdentityPromptInfoDTO(
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-sg.png",
                    "Singapore NRIC/ Driver's License");
        }
        else
        {
            throw new IllegalArgumentException("Unhandled pretend country " + pretendInCountry);
        }
        return identityPromptInfo;
    }

    @NonNull public List<Country> createNoBusinessNationalities()
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
