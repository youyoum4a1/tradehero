package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.ObjectMapperWrapper;
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.IdentityPromptInfoDTO;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.KYCFormOptionsId;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class DummyLiveServiceWrapper extends LiveServiceWrapper
{
    private static final int TIME_OUT_SECONDS = 5;
    private final Country pretendInCountry = Country.SG;

    @NonNull private final ObjectMapperWrapper objectMapperWrapper;

    @Inject public DummyLiveServiceWrapper(
            @NonNull LiveServiceRx liveServiceRx,
            @NonNull LiveServiceAyondoRx liveServiceAyondoRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull ObjectMapperWrapper objectMapperWrapper,
            @NonNull PhoneNumberVerifiedPreference phoneNumberVerifiedPreference)
    {
        super(liveServiceRx, liveServiceAyondoRx, liveBrokerSituationPreference, phoneNumberVerifiedPreference);
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @NonNull @Override public Observable<LiveTradingSituationDTO> getLiveTradingSituation()
    {
        return super.getLiveTradingSituation()
                .doOnNext(new Action1<LiveTradingSituationDTO>()
                {
                    @Override public void call(LiveTradingSituationDTO liveTradingSituationDTO)
                    {
                        if (liveTradingSituationDTO.brokerSituations.isEmpty())
                        {
                            LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "ayondo markets");
                            KYCAyondoForm form = new KYCAyondoForm();
                            form.setCountry(Country.SG);
                            form.setStepStatuses(DummyKYCAyondoUtil.getSteps(form).stepStatuses);
                            LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);

                            liveTradingSituationDTO.brokerSituations.add(fakeSituation);
                        }
                    }
                })
                .map(new Func1<LiveTradingSituationDTO, LiveTradingSituationDTO>()
                {
                    @Override public LiveTradingSituationDTO call(LiveTradingSituationDTO liveTradingSituationDTO)
                    {
                        for (LiveBrokerSituationDTO situationDTO : liveTradingSituationDTO.brokerSituations)
                        {
                            if (situationDTO.kycForm != null)
                            {
                                if (situationDTO.kycForm.getCountry() == null)
                                {
                                    ((KYCAyondoForm) situationDTO.kycForm).setCountry(Country.SG);
                                }
                                List<StepStatus> stepStatuses = situationDTO.kycForm.getStepStatuses();
                                if (situationDTO.kycForm instanceof KYCAyondoForm
                                        && (stepStatuses == null || stepStatuses.size() != 5))
                                {
                                    situationDTO.kycForm.setStepStatuses(
                                            DummyKYCAyondoUtil.getSteps((KYCAyondoForm) situationDTO.kycForm).stepStatuses);
                                }
                            }
                        }
                        return liveTradingSituationDTO;
                    }
                })
                .doOnNext(new Action1<LiveTradingSituationDTO>()
                {
                    @Override public void call(LiveTradingSituationDTO liveTradingSituationDTO)
                    {
                        for (LiveBrokerSituationDTO situationDTO : liveTradingSituationDTO.brokerSituations)
                        {
                            //noinspection ConstantConditions
                            if (situationDTO.kycForm.getStepStatuses().isEmpty())
                            {
                                if (situationDTO.kycForm instanceof KYCAyondoForm)
                                {
                                    situationDTO.kycForm.setStepStatuses(
                                            DummyKYCAyondoUtil.getSteps((KYCAyondoForm) situationDTO.kycForm).stepStatuses);
                                }
                                else
                                {
                                    situationDTO.kycForm.setStepStatuses(
                                            Arrays.asList(StepStatus.UNSTARTED, StepStatus.COMPLETE, StepStatus.UNSTARTED, StepStatus.UNSTARTED,
                                                    StepStatus.UNSTARTED));
                                }
                            }
                        }
                    }
                })
                .timeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .onErrorResumeNext(
                        new Func1<Throwable, Observable<? extends LiveTradingSituationDTO>>()
                        {
                            @Override public Observable<? extends LiveTradingSituationDTO> call(Throwable throwable)
                            {
                                LiveBrokerDTO ayondo = new LiveBrokerDTO(new LiveBrokerId(1), "ayondo markets");
                                KYCAyondoForm form = new KYCAyondoForm();
                                form.setStepStatuses(
                                        Arrays.asList(StepStatus.UNSTARTED, StepStatus.COMPLETE, StepStatus.UNSTARTED, StepStatus.UNSTARTED,
                                                StepStatus.UNSTARTED));
                                form.setCountry(Country.SG);
                                LiveBrokerSituationDTO fakeSituation = new LiveBrokerSituationDTO(ayondo, form);
                                return Observable.just(new LiveTradingSituationDTO(Collections.singletonList(fakeSituation)));
                            }
                        });
    }

    @NonNull @Override public Observable<StepStatusesDTO> applyToLiveBroker(@NonNull LiveBrokerId brokerId, @NonNull final KYCForm kycForm)
    {
        return super.applyToLiveBroker(brokerId, kycForm)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends StepStatusesDTO>>()
                {
                    @Override public Observable<? extends StepStatusesDTO> call(Throwable throwable)
                    {
                        if (kycForm instanceof KYCAyondoForm)
                        {
                            return Observable.just(DummyKYCAyondoUtil.getSteps((KYCAyondoForm) kycForm));
                        }
                        StepStatusesDTO stepStatusesDTO = new StepStatusesDTO(
                                Arrays.asList(StepStatus.UNSTARTED, StepStatus.COMPLETE, StepStatus.UNSTARTED, StepStatus.UNSTARTED,
                                        StepStatus.UNSTARTED));
                        return Observable.just(stepStatusesDTO);
                    }
                });
    }

    @NonNull @Override public Observable<KYCFormOptionsDTO> getKYCFormOptions(@NonNull KYCFormOptionsId optionsId)
    {
        return super.getKYCFormOptions(optionsId)
                .timeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
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
                                        21);
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
