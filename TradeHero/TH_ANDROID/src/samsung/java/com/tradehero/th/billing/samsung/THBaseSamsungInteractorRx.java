package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THSamsungSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THSamsungStoreProductDetailView;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;

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
                THSamsungLogicHolderRx,
                THSamsungStoreProductDetailView,
                THSamsungSKUDetailAdapter>
        implements THSamsungInteractorRx
{
    public static final String BUNDLE_KEY_ACTION = THBaseSamsungInteractorRx.class.getName() + ".action";

    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THSamsungAlertDialogRxUtil thSamsungAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull THSamsungLogicHolderRx billingActor,
            @NonNull CurrentUserId currentUserId,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thSamsungAlertDialogUtil,
                billingRequisitePreparer);
        this.currentUserId = currentUserId;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @Override @NonNull public String getName()
    {
        return SamsungConstants.NAME;
    }

    @NonNull @Override public Observable<THSamsungPurchaseOrder> createPurchaseOrder(
            @NonNull ProductInventoryResult<SamsungSKU, THSamsungProductDetail> inventoryResult)
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .take(1)
                .map(pair -> pair.second.getDefaultPortfolio())
                .flatMap(dto -> {
                    if (dto == null)
                    {
                        return Observable.error(new IllegalArgumentException("Default portfolio cannot be null"));
                    }
                    return Observable.just(dto);
                })
                .map(dto -> new THSamsungPurchaseOrder(
                        inventoryResult.id.groupId,
                        inventoryResult.id.itemId,
                        dto.getOwnedPortfolioId()));
    }
}
