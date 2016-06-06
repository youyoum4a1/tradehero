package com.androidth.general.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherHolderRx;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
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
