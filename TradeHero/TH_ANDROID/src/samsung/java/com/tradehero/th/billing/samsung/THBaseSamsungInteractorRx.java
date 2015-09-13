package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Func1;

public class THBaseSamsungInteractorRx
        extends
        THBaseBillingInteractorRx<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolderRx>
        implements THSamsungInteractorRx
{
    public static final String BUNDLE_KEY_ACTION = THBaseSamsungInteractorRx.class.getName() + ".action";

    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull THSamsungAlertDialogRxUtil thSamsungAlertDialogUtil,
            @NonNull THSamsungLogicHolderRx billingActor,
            @NonNull CurrentUserId currentUserId,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super(
                billingActor,
                activityProvider,
                thSamsungAlertDialogUtil);
        this.currentUserId = currentUserId;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @Override @NonNull public String getName()
    {
        return SamsungConstants.NAME;
    }

    @NonNull protected Observable<PortfolioCompactDTO> getDefaultPortfolio()
    {
        return portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                .flatMap(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, Observable<? extends PortfolioCompactDTO>>()
                {
                    @Override public Observable<? extends PortfolioCompactDTO> call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                    {
                        PortfolioCompactDTO dto = pair.second.getDefaultPortfolio();
                        if (dto == null)
                        {
                            return Observable.error(new IllegalArgumentException("Default portfolio cannot be null"));
                        }
                        return Observable.just(dto);
                    }
                });
    }

    @NonNull @Override public Observable<THSamsungPurchaseOrder> createPurchaseOrder(
            @NonNull final THSamsungProductDetail detail)
    {
        return getDefaultPortfolio()
                .map(new Func1<PortfolioCompactDTO, THSamsungPurchaseOrder>()
                {
                    @Override public THSamsungPurchaseOrder call(PortfolioCompactDTO dto)
                    {
                        return new THSamsungPurchaseOrder(
                                detail.getProductIdentifier(),
                                dto.getOwnedPortfolioId());
                    }
                });
    }

    @NonNull @Override public Observable<THSamsungPurchaseOrder> createPurchaseOrder(
            @NonNull final THSamsungProductDetail detail,
            @NonNull final UserBaseKey heroId)
    {
        return getDefaultPortfolio()
                .map(new Func1<PortfolioCompactDTO, THSamsungPurchaseOrder>()
                {
                    @Override public THSamsungPurchaseOrder call(PortfolioCompactDTO dto)
                    {
                        return new THSamsungPurchaseOrder(
                                detail.getProductIdentifier(),
                                1,
                                dto.getOwnedPortfolioId(),
                                heroId);
                    }
                });
    }

    @Override public void manageSubscriptions()
    {
        THToast.show("TODO");
    }
}
