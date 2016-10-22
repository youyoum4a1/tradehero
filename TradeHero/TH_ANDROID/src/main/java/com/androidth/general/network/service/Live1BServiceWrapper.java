package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.androidth.general.api.kyc.AnnualIncomeRange;
import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.Currency;
import com.androidth.general.api.kyc.EmploymentStatus;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.KYCFormOptionsId;
import com.androidth.general.api.kyc.LiveAvailabilityDTO;
import com.androidth.general.api.kyc.NetWorthRange;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import com.androidth.general.api.kyc.PhoneNumberVerifiedStatusDTO;
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
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.live1b.NewOrderSingleDTO;
import com.androidth.general.api.live1b.OrderSideEnum;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.TransactionFormDTO;
import com.androidth.general.api.security.key.SearchSecurityListType;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.api.security.key.TrendingAllSecurityListType;
import com.androidth.general.api.security.key.TrendingBasicSecurityListType;
import com.androidth.general.api.security.key.TrendingPriceSecurityListType;
import com.androidth.general.api.security.key.TrendingSecurityListType;
import com.androidth.general.api.security.key.TrendingVolumeSecurityListType;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.fastfill.Gender;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ResidenceScannedDocumentType;
import com.androidth.general.models.security.DTOProcessorSecurityPositionTransactionUpdated;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.service.ayondo.LiveServiceAyondoRx;
import com.androidth.general.persistence.portfolio.PortfolioCacheRx;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.persistence.prefs.PhoneNumberVerifiedPreference;
import com.androidth.general.utils.LiveConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import timber.log.Timber;


@Singleton
public class Live1BServiceWrapper {
    private static final int AYONDO_MINIMUM_AGE = 21;

    // for dynamic query
    // enum?
    public static final String PROVIDER_ID = "{providerId}";
    public static final String INPUT = "{input}";
    @NonNull
    private final Live1BServiceRx live1BServiceRx;
    @NonNull
    private final LiveServiceAyondoRx liveServiceAyondoRx;
    @NonNull
    private final LiveBrokerSituationPreference liveBrokerSituationPreference;
    @NonNull
    private final PhoneNumberVerifiedPreference phoneNumberVerifiedPreference;

    @NonNull
    private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull
    private final CurrentUserId currentUserId;

    @Inject
    public Live1BServiceWrapper(
            @NonNull Live1BServiceRx live1BServiceRx,
            @NonNull LiveServiceAyondoRx liveServiceAyondoRx,
            @NonNull LiveBrokerSituationPreference liveBrokerSituationPreference,
            @NonNull PhoneNumberVerifiedPreference phoneNumberVerifiedPreference,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId) {
        this.live1BServiceRx = live1BServiceRx;
        this.liveServiceAyondoRx = liveServiceAyondoRx;
        this.liveBrokerSituationPreference = liveBrokerSituationPreference;
        this.phoneNumberVerifiedPreference = phoneNumberVerifiedPreference;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }

    @NonNull
    public Observable<LiveAvailabilityDTO> getAvailability() {
        return liveServiceAyondoRx.getAvailability()
                .cast(LiveAvailabilityDTO.class);
        // .merge() with other specific ones when time comes
    }

    @NonNull
    public Observable<LiveTradingSituationDTO> getLiveTradingSituation() {
        //Generic calls for multi brokers
        return live1BServiceRx.getLiveTradingSituation();
    }

//    @NonNull public Observable<StepStatusesDTO> applyToLiveBroker(
//            @NonNull LiveBrokerId brokerId,
//            @NonNull KYCForm kycForm)
//    {
//        if (brokerId.key.equals(LiveBrokerKnowledge.BROKER_ID_AYONDO))
//        {
//            return liveServiceAyondoRx.applyLiveBroker(kycForm);
//        }
//        return liveServiceRx.applyLiveBroker(brokerId.key, kycForm);
//    }

    //<editor-fold desc="Buy or Sell Security">
    @NonNull
    public Observable<String> doTransactionRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO,
            boolean isBuy) {
//        if (isBuy)
//        {
//            return buyRx(securityId, transactionFormDTO);
//        }
//        return sellRx(securityId, transactionFormDTO);
        OrderSideEnum orderSide = isBuy ? OrderSideEnum.getOrderSideEnumFromId('1') : OrderSideEnum.getOrderSideEnumFromId('2');

        return buyNewOrder(String.valueOf(securityId.getAyondoId()), orderSide, transactionFormDTO.quantity);
    }
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @NonNull public Observable<SecurityPositionTransactionDTO> buyRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO)
    {
        Observable<SecurityPositionTransactionDTO> buyResult;
//        if (securityId.getExchange().equals(SecurityUtils.FX_EXCHANGE))
//        {
//            buyResult = this.live1BServiceRx.buy.securityServiceRx.buyFx(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
//        }
//        else
//        {
//            buyResult = this.securityServiceRx.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
//        }

        buyResult = this.live1BServiceRx.buy(securityId.getExchange(),securityId.getSecuritySymbol(),transactionFormDTO);
        return buyResult
                .map(new DTOProcessorSecurityPositionTransactionUpdated(
                        securityId,
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get()));
    }
    //</editor-fold>


    //<editor-fold desc="New Order Single Security">
    @NonNull public Observable<String> buyNewOrder(
            @NonNull String securityId,
            @NonNull OrderSideEnum orderSideEnum,
            @NonNull float quantity)
    {
        Observable<String> buyResult;

        NewOrderSingleDTO newOrderSingleDTO = new NewOrderSingleDTO(securityId,orderSideEnum, quantity);

        buyResult = this.live1BServiceRx.newOrder(newOrderSingleDTO);

        Log.d(".java", "buyNewOrder: securityId used for order: " + securityId + " islive? : " + LiveConstants.isInLiveMode);
        return buyResult;
    }
    //</editor-fold>

    @NonNull public Observable<String> getPositions()
    {
        Log.d(".java", "getPositions() is live mode? : " + LiveConstants.isInLiveMode);
        return this.live1BServiceRx.getOMPositions("");
    }

    //    //<editor-fold desc="Sell Security">
    @NonNull public Observable<SecurityPositionTransactionDTO> sellRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO)
    {
        Observable<SecurityPositionTransactionDTO> sellResult;
//        if (securityId.getExchange().equals(SecurityUtils.FX_EXCHANGE))
//        {
//            sellResult = this.securityServiceRx.sellFx(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
//        }
//        else
//        {
//            sellResult = this.securityServiceRx.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
//        }
        // ToDo update to .sell when the URL is working for SELL
        sellResult = this.live1BServiceRx.buy(securityId.getExchange(),securityId.getSecuritySymbol(),transactionFormDTO);
        return sellResult
                .map(new DTOProcessorSecurityPositionTransactionUpdated(
                        securityId,
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get()));
    }
    //</editor-fold>


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

    // design get result from server but now is all from client, to have a better design??
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
                Arrays.asList("asdf"),
                Arrays.asList("Online", "Events"));
        return Observable.just(options);
    }

    //@NonNull public Observable<KYCFormOptionsDTO> getKYCForm(@NonNull KYC)

//    @NonNull public Observable<IdentityPromptInfoDTO> getIdentityPromptInfo(@NonNull Country country)
//    {
//        return Observable.just(new IdentityPromptInfoDTO(Country.SG, "fake", "Wait until we support more countries"));
//    }

    @NonNull public Observable<PhoneNumberVerifiedStatusDTO> getPhoneNumberVerifiedStatus(@NonNull String phoneNumber)
    {
        return Observable.just(new PhoneNumberVerifiedStatusDTO(
                phoneNumber,
                phoneNumberVerifiedPreference.get().contains(phoneNumber)));
    }

    @NonNull public Observable<Boolean>validateData(@NonNull String url, Map<String, String> parameter) {
        String partialURL = url.replace(LiveNetworkConstants.TRADEHERO_LIVE_API_ENDPOINT, "");

        for (Map.Entry<String, String> query: parameter.entrySet())
        {
            partialURL = partialURL.replace(query.getKey(), query.getValue());
        }

        return live1BServiceRx.validateData(partialURL);
    }


    public void submitPhoneNumberVerifiedStatus(String formattedPhoneNumber)
    {
        phoneNumberVerifiedPreference.addVerifiedNumber(formattedPhoneNumber);
    }

    public Observable<BrokerApplicationDTO> createOrUpdateLead(int providerId, KYCForm kycForm)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            //TODO change to specific class
            Timber.d("Creating or updating lead..");
            return liveServiceAyondoRx.createOrUpdateLead(providerId, new AyondoLeadDTO((KYCAyondoForm) kycForm));
        }
        else
        {
            //TODO when we have multiple brokers
            Timber.d("Creating or updating lead multiple..");
            return Observable.just(null);
        }
    }

//    public Observable<BrokerDocumentUploadResponseDTO> uploadDocument(File f)
//    {
//        return liveServiceRx.uploadDocument(GraphicUtil.fromFile(f));
//    }

    public Observable<AyondoIDCheckDTO> checkNeedIdentityDocument(AyondoLeadUserIdentityDTO ayondoLeadUserIdentityDTO)
    {
        return liveServiceAyondoRx.checkNeedIdentity(ayondoLeadUserIdentityDTO);
    }

    public Observable<AyondoAddressCheckDTO> checkNeedResidencyDocument(AyondoLeadAddressDTO ayondoLeadAddressDTO)
    {
        return liveServiceAyondoRx.checkNeedResidency(ayondoLeadAddressDTO);
    }

    public Observable<BrokerApplicationDTO> submitApplication(KYCForm kycForm, int providerId)
    {
        if (kycForm instanceof KYCAyondoForm)
        {
            return liveServiceAyondoRx.submitApplication(new AyondoAccountCreationDTO((KYCAyondoForm) kycForm), providerId);
        }
        else
        {
            //TODO when we have multiple brokers
            return Observable.just(null);
        }
    }

    //<editor-fold desc="Get Securities">
    @NonNull public Observable<SecurityCompactDTOList> getSecuritiesRx(@NonNull SecurityListType key)
    {
        Observable<SecurityCompactDTOList> received = null;
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                received = this.live1BServiceRx.getTrendingSecurities(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                received = this.live1BServiceRx.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received = this.live1BServiceRx.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                received = this.live1BServiceRx.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
//        else if (key instanceof TrendingFxSecurityListType)
//        {
//            // FIXME when the server offers pagination
//            if (key.page != null && key.page == 1)
//            {
//                received = this.live1BServiceRx.getFXSecurities();
//            }
//            else
//            {
//                received = Observable.just(new SecurityCompactDTOList());
//            }
//        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received = this.live1BServiceRx.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
//        else if (key instanceof ProviderSecurityListType)
//        {
//            received = providerServiceWrapper.getProviderSecuritiesRx((ProviderSecurityListType) key);
//        }
//        else if (key instanceof ExchangeSectorSecurityListType)
//        {
//            ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
//            received = this.securityServiceRx.getBySectorAndExchange(
//                    exchangeKey.exchangeId == null ? null : exchangeKey.exchangeId.key,
//                    exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
//                    key.page,
//                    key.perPage);
//        }
//        else if (key instanceof ExchangeSectorSecurityListTypeNew)
//        {
//            ExchangeSectorSecurityListTypeNew exchangeKey = (ExchangeSectorSecurityListTypeNew) key;
//            received = this.securityServiceRx.getBySectorsAndExchanges(
//                    exchangeKey.getCommaSeparatedExchangeIds(),
//                    exchangeKey.getCommaSeparatedSectorIds(),
//                    exchangeKey.getPage(),
//                    exchangeKey.perPage);
//        }
//        else
//        {
//            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
//        }
        return received;
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
