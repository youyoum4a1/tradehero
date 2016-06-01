package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.key.ExchangeSectorSecurityListType;
import com.ayondo.academy.api.security.key.ExchangeSectorSecurityListTypeNew;
import com.ayondo.academy.api.security.key.SecurityListType;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.persistence.portfolio.PortfolioCacheRx;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SecurityServiceWrapperStub extends SecurityServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapperStub(
            @NonNull SecurityServiceRx securityServiceRx,
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId)
    {
        super(securityServiceRx, providerServiceWrapper,
                securityCompactCache, portfolioCache,
                currentUserId);
    }
    //</editor-fold>

    @NonNull @Override public Observable<SecurityCompactDTOList> getSecuritiesRx(@NonNull final SecurityListType key)
    {
        return super.getSecuritiesRx(key)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends SecurityCompactDTOList>>()
                {
                    @Override public Observable<? extends SecurityCompactDTOList> call(Throwable throwable)
                    {
                        // While server is not set
                        if (key instanceof ExchangeSectorSecurityListTypeNew)
                        {
                            ExchangeSectorSecurityListTypeNew exchangeKey = (ExchangeSectorSecurityListTypeNew) key;
                            return getSecuritiesRx(new ExchangeSectorSecurityListType(
                                    exchangeKey.exchangeIds == null || exchangeKey.exchangeIds.isEmpty() ? null : exchangeKey.exchangeIds.get(0),
                                    exchangeKey.sectorIds == null || exchangeKey.sectorIds.isEmpty() ? null : exchangeKey.sectorIds.get(0),
                                    exchangeKey.getPage(),
                                    exchangeKey.perPage));
                        }
                        else
                        {
                            return Observable.error(throwable);
                        }
                    }
                });
    }
}
