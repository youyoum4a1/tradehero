package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.KYCFormOptionsDTO;
import com.tradehero.th.api.live.KYCFormOptionsId;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.api.live.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class DummyLiveServiceWrapper extends LiveServiceWrapper
{
    private final Country pretendInCountry = Country.SG;

    @Inject public DummyLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference)
    {
        super(liveServiceRx, liveBrokerSituationPreference);
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return super.getLiveTradingSituation()
                .timeout(1, TimeUnit.SECONDS)
                .onErrorResumeNext(
                        new Func1<Throwable, Observable<? extends LiveTradingSituationDTO>>()
                        {
                            @Override public Observable<? extends LiveTradingSituationDTO> call(Throwable throwable)
                            {
                                LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "Ayondo");
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
                .timeout(1, TimeUnit.SECONDS)
                .onErrorResumeNext(
                        new Func1<Throwable, Observable<? extends KYCFormOptionsDTO>>()
                        {
                            @Override public Observable<? extends KYCFormOptionsDTO> call(Throwable throwable)
                            {
                                List<Country> nationalities = new ArrayList<>(Arrays.asList(Country.values()));
                                nationalities.remove(Country.NONE);
                                nationalities.removeAll(createNoBusinessNationalities());
                                KYCFormOptionsDTO options = new KYCAyondoFormOptionsDTO(
                                        createIdentityPromptInfo(),
                                        Arrays.asList(Country.SG, Country.AU, Country.GB),
                                        nationalities,
                                        Arrays.asList(Country.SG, Country.AU, Country.GB),
                                        Arrays.asList("Less than $15,000", "$15,000 - $40,000", "$40,001 - $70,000", "$70,001 - $100,000",
                                                "more than $100,000"),
                                        Arrays.asList("Less than $15,000", "$15,000 - $40,000", "$40,001 - $70,000", "$70,001 - $100,000",
                                                "$100,001 - $500,000", "more than $500,000"),
                                        Arrays.asList("Less than 25 %", "25 - 50 %", "51 - 75 %", "more than 75%"),
                                        Arrays.asList("Employed", "Self-Employed", "Unemployed", "Retired", "Student"));
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
