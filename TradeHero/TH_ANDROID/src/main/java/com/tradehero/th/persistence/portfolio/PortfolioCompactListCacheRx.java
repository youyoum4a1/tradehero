package com.tradehero.th.persistence.portfolio;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

@Singleton @UserCache
public class PortfolioCompactListCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, PortfolioCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<PortfolioCompactCacheRx> portfolioCompactCache;
    @NotNull protected final Lazy<PortfolioCacheRx> portfolioCache;
    @NotNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioCompactListCacheRx(
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<PortfolioCompactCacheRx> portfolioCompactCache,
            @NotNull Lazy<PortfolioCacheRx> portfolioCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<PortfolioCompactDTOList> fetch(@NotNull UserBaseKey key)
    {
        return portfolioServiceWrapper.get().getPortfoliosRx(key, key.equals(currentUserId.toUserBaseKey()));
    }

    @Override public void onNext(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
    {
        portfolioCompactCache.get().onNext(value);
        super.onNext(key, value);
    }

    @Override public void invalidate(@NotNull UserBaseKey key)
    {
        @Nullable PortfolioCompactDTOList value = getValue(key);
        if (value != null)
        {
            for (@NotNull PortfolioCompactDTO portfolioCompactDTO : value)
            {
                portfolioCompactCache.get().invalidate(portfolioCompactDTO.getPortfolioId());
                portfolioCache.get().invalidate(new OwnedPortfolioId(key.key, portfolioCompactDTO.id));
            }
        }
    }

    @Nullable public Observable<PortfolioCompactDTO> getDefaultPortfolio(@NotNull UserBaseKey key)
    {
        return get(key)
                .filter(pair -> pair.second.size() != 0)
                .flatMap(pair -> {
                    PortfolioCompactDTO defaultPortfolio = pair.second.getDefaultPortfolio();
                    if (defaultPortfolio != null)
                    {
                        return Observable.just(defaultPortfolio);
                    }
                    return Observable.empty();
                });
    }
}
