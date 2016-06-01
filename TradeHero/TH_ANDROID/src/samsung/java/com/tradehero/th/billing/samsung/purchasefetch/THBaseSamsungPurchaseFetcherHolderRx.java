package com.ayondo.academy.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherHolderRx;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOList;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import com.ayondo.academy.persistence.portfolio.PortfolioCompactListCacheRx;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import javax.inject.Inject;
import rx.functions.Action1;

public class THBaseSamsungPurchaseFetcherHolderRx
        extends BaseSamsungPurchaseFetcherHolderRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaseFetcherHolderRx
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;
    protected OwnedPortfolioId defaultPortfolioId;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcherHolderRx(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull CurrentUserId currentUserId,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super();
        this.context = context;
        this.mode = mode;
        portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                .subscribe(
                        new Action1<Pair<UserBaseKey, PortfolioCompactDTOList>>()
                        {
                            @Override public void call(Pair<UserBaseKey, PortfolioCompactDTOList> userBaseKeyPortfolioCompactDTOListPair)
                            {
                                defaultPortfolioId = userBaseKeyPortfolioCompactDTOListPair.second.getDefaultPortfolio().getOwnedPortfolioId();
                            }
                        },
                        new ToastOnErrorAction1());
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungPurchaseFetcherRx createFetcher(int requestCode)
    {
        return new THBaseSamsungPurchaseFetcherRx(requestCode, context, mode, defaultPortfolioId);
    }
}
