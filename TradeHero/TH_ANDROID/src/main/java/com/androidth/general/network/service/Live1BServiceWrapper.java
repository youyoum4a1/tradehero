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
import com.androidth.general.api.users.UserLiveAccount;
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
    @NonNull
    private final Live1BServiceRx live1BServiceRx;

    // for dynamic query
    // enum?
//    public static final String PROVIDER_ID = "{providerId}";
//    public static final String INPUT = "{input}";

//    @NonNull
//    private final LiveServiceAyondoRx liveServiceAyondoRx;
//    @NonNull
//    private final LiveBrokerSituationPreference liveBrokerSituationPreference;
//    @NonNull
//    private final PhoneNumberVerifiedPreference phoneNumberVerifiedPreference;
//
    @NonNull
    private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull
    private final CurrentUserId currentUserId;

    @Inject
    public Live1BServiceWrapper(
            @NonNull Live1BServiceRx live1BServiceRx,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId){

        this.live1BServiceRx = live1BServiceRx;
//        this.liveServiceAyondoRx = liveServiceAyondoRx;
//        this.liveBrokerSituationPreference = liveBrokerSituationPreference;
//        this.phoneNumberVerifiedPreference = phoneNumberVerifiedPreference;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }

    @NonNull
    public Observable<UserLiveAccount> getUserLiveAccount() {
        return live1BServiceRx.getUserLiveAccount()
                .cast(UserLiveAccount.class);
    }

    @NonNull
    public Observable<LiveTradingSituationDTO> getLiveTradingSituation() {
        //Generic calls for multi brokers
        return live1BServiceRx.getLiveTradingSituation();
    }

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

        return buySellNewOrder(String.valueOf(securityId.getAyondoId()), orderSide, transactionFormDTO.quantity);
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
    @NonNull public Observable<String> buySellNewOrder(
            @NonNull String securityId,
            @NonNull OrderSideEnum orderSideEnum,
            @NonNull float quantity)
    {
        Observable<String> buySellResult;

        NewOrderSingleDTO newOrderSingleDTO = new NewOrderSingleDTO(securityId,orderSideEnum, quantity);

        buySellResult = this.live1BServiceRx.newOrder(newOrderSingleDTO);

        Log.d(".java", "buyNewOrder: securityId used for order: " + securityId + " islive? : " + LiveConstants.isInLiveMode);
        return buySellResult;
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




//    public Observable<BrokerDocumentUploadResponseDTO> uploadDocument(File f)
//    {
//        return liveServiceRx.uploadDocument(GraphicUtil.fromFile(f));
//    }

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
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received = this.live1BServiceRx.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
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
