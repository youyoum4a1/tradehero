package com.androidth.general.persistence.portfolio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioDTOList;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class PortfolioCacheRx extends BaseFetchDTOCacheRx<OwnedPortfolioId, PortfolioDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;

    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCacheRx(
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PortfolioDTO> fetch(@NonNull OwnedPortfolioId key)
    {
        return portfolioServiceWrapper.get().getPortfolioRx(key);
    }

    @Override public void onNext(@NonNull OwnedPortfolioId key, @NonNull PortfolioDTO value)
    {
        PortfolioDTO previous = getCachedValue(key);
        //noinspection ConstantConditions
        if (previous != null && previous.userId != null)
        {
            value.userId = previous.userId;
        }
        //noinspection ConstantConditions
        if (value.userId == null)
        {
            throw new NullPointerException("UserId should be set");
        }
        portfolioCompactCache.get().onNext(key.getPortfolioIdKey(), value);
        super.onNext(key, value);
    }

    @NonNull public Observable<PortfolioDTOList> getPortfolios(
            @NonNull List<? extends PortfolioCompactDTO> portfolioCompactDTOs, @SuppressWarnings(
            "UnusedParameters") @Nullable PortfolioCompactDTO typeQualifier)
    {
        return getPortfolios(new OwnedPortfolioIdList(portfolioCompactDTOs, null), null);
    }

    @NonNull public Observable<PortfolioDTOList> getPortfolios(
            @NonNull List<? extends OwnedPortfolioId> ownedPortfolioIds, @SuppressWarnings(
            "UnusedParameters") @Nullable OwnedPortfolioId typeQualifier)
    {
        return Observable.from(ownedPortfolioIds)
                .flatMap(new Func1<OwnedPortfolioId, Observable<? extends Pair<OwnedPortfolioId, PortfolioDTO>>>()
                {
                    @Override public Observable<? extends Pair<OwnedPortfolioId, PortfolioDTO>> call(OwnedPortfolioId id)
                    {
                        return PortfolioCacheRx.this.get(id).take(1);
                    }
                })
                .map(new PairGetSecond<OwnedPortfolioId, PortfolioDTO>())
                .toList()
                .map(new Func1<List<PortfolioDTO>, PortfolioDTOList>()
                {
                    @Override public PortfolioDTOList call(List<PortfolioDTO> t1)
                    {
                        return new PortfolioDTOList(t1);
                    }
                });
    }

    public void invalidate(@NonNull UserBaseKey concernedUser)
    {
        invalidate(concernedUser, false);
    }

    public void invalidate(@NonNull UserBaseKey concernedUser, boolean onlyWatchlist)
    {
        PortfolioDTO cached;
        for (OwnedPortfolioId key : snapshot().keySet())
        {
            cached = getCachedValue(key);
            if (cached != null
                    && key.userId.equals(concernedUser.key)
                    && (cached.isWatchlist || !onlyWatchlist))
            {
                invalidate(key);
            }
        }
    }
}
