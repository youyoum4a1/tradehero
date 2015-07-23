package com.tradehero.th.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherHolderRx;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction1;
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
