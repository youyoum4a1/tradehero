package com.tradehero.th.network.service;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.IdentityPromptInfoKey;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.LiveCountryDTO;
import com.tradehero.th.api.live.LiveCountryDTOList;
import com.tradehero.th.api.live.LiveCountryListId;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.persistence.prefs.KYCFormPreference;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import java.util.Collections;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class DummyLiveServiceWrapper extends LiveServiceWrapper
{
    @Inject public DummyLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull KYCFormPreference kycFormPreference)
    {
        super(liveServiceRx, kycFormPreference);
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "Ayondo");
        KYCAyondoForm form = new KYCAyondoForm();
        form.setCountry(Country.SG);
        LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);
        return Observable.just(new LiveTradingSituationDTO(Collections.singletonList(fakeSituation)));
    }

    @NonNull @Override public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(IdentityPromptInfoKey identityPromptInfoKey)
    {
        IdentityPromptInfoDTO infoDTO = new IdentityPromptInfoDTO();
        if (identityPromptInfoKey.country.equals(Country.AU))
        {
            infoDTO.image = "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-au.png";
            infoDTO.prompt = "Do you have your Passport with you?";
        }
        else if (identityPromptInfoKey.country.equals(Country.SG))
        {
            infoDTO.image =
                    "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-sg.png";
            infoDTO.prompt = "Do you have your Passport/ Singapore NRIC/ Driver's License with you?";
        }
        return Observable.just(infoDTO);
    }

    @Override public Observable<LiveCountryDTOList> getLiveCountryList(LiveCountryListId key)
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

    @NonNull @Override public Observable<KYCForm> getFormToUse(@NonNull final Activity activity)
    {
        return super.getFormToUse(activity)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<KYCForm, Observable<KYCForm>>()
                {
                    @Override public Observable<KYCForm> call(final KYCForm kycForm)
                    {
                        return AlertDialogRxUtil.build(activity)
                                .setTitle("Fake country")
                                .setPositiveButton("SG")
                                .setNegativeButton("AU")
                                .build()
                                .map(new Func1<OnDialogClickEvent, KYCForm>()
                                {
                                    @Override public KYCForm call(OnDialogClickEvent clickEvent)
                                    {
                                        if (clickEvent.isPositive())
                                        {
                                            ((KYCAyondoForm) kycForm).setCountry(Country.SG);
                                        }
                                        else if (clickEvent.isNegative())
                                        {
                                            ((KYCAyondoForm) kycForm).setCountry(Country.AU);
                                        }
                                        return kycForm;
                                    }
                                });
                    }
                });
    }
}
