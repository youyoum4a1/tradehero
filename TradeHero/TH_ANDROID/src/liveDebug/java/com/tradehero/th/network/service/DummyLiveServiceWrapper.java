package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.IdentityPromptInfoKey;
import com.tradehero.th.api.live.KYCFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.api.live.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class DummyLiveServiceWrapper extends LiveServiceWrapper
{
    @Inject public DummyLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference)
    {
        super(liveServiceRx, liveBrokerSituationPreference);
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "Ayondo");
        KYCAyondoForm form = new KYCAyondoForm();
        form.setCountry(Country.SG);
        LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);
        return Observable.just(new LiveTradingSituationDTO(Collections.singletonList(fakeSituation)));
    }

    @NonNull @Override public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(@NonNull IdentityPromptInfoKey identityPromptInfoKey)
    {
        final Observable<IdentityPromptInfoDTO> infoDTOObservable;
        if (identityPromptInfoKey.country.equals(Country.AU))
        {
            infoDTOObservable = Observable.just(new IdentityPromptInfoDTO(
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-au.png",
                    "Do you have your Passport with you?"));
        }
        else if (identityPromptInfoKey.country.equals(Country.SG))
        {
            infoDTOObservable = Observable.just(new IdentityPromptInfoDTO(
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-sg.png",
                    "Singapore NRIC/ Driver's License"));
        }
        else
        {
            infoDTOObservable = Observable.error(new IllegalArgumentException("Unhandled country " + identityPromptInfoKey.country));
        }
        return infoDTOObservable;
    }

    @Override @NonNull public Observable<LiveCountryDTOList> getLiveCountryList()
    {
        LiveCountryDTOList liveCountryDTOs = new LiveCountryDTOList();
        Country[] values = Country.values();
        for (int i = 1; i < values.length; i++)
        {
            Country country = values[i];
            liveCountryDTOs.add(new LiveCountryDTO(country));
        }
        return Observable.just(liveCountryDTOs);
    }

    @NonNull @Override public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull LiveBrokerId liveBrokerId)
    {
        List<Country> nationalities = new ArrayList<>(Arrays.asList(Country.values()));
        nationalities.remove(Country.NONE);
        nationalities.removeAll(getNoBusinessNationalities());
        KYCFormOptionsDTO options = new KYCAyondoFormOptionsDTO(
                Arrays.asList(Country.SG, Country.AU, Country.GB),
                nationalities,
                Arrays.asList(Country.SG, Country.AU, Country.GB));
        return Observable.just(options);
    }

    @NonNull public List<Country> getNoBusinessNationalities()
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
