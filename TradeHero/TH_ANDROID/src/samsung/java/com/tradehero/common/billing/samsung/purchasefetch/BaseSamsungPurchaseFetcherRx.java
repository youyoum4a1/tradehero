package com.tradehero.common.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.rx.InboxListQueryGroup;
import com.tradehero.common.billing.samsung.rx.SamsungInboxOperatorZip;
import java.util.List;
import rx.Observable;

abstract public class BaseSamsungPurchaseFetcherRx<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungPurchaseIncompleteType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BaseSamsungActorRx<PurchaseFetchResult<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>>
        implements SamsungPurchaseFetcherRx<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode)
    {
        super(requestCode, context, mode);
        fetchPurchases();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseFetchResult<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType>> get()
    {
        return replayObservable;
    }

    protected void fetchPurchases()
    {
        new SamsungInboxOperatorZip(context, mode, getInboxListQueryGroups())
                .getInboxItems()
                .flatMap(pair -> pair.second
                        .map(inboxVo -> createIncompletePurchase(pair.first, inboxVo)))
                .map(this::mergeWithSaved)
                .map(purchase -> new PurchaseFetchResult<>(getRequestCode(), purchase))
                .subscribe(subject);
    }

    @NonNull abstract protected List<InboxListQueryGroup> getInboxListQueryGroups();

    @NonNull abstract protected SamsungPurchaseIncompleteType createIncompletePurchase(
            @NonNull InboxListQueryGroup queryGroup,
            @NonNull InboxVo inboxVo);

    @NonNull abstract protected SamsungPurchaseType mergeWithSaved(
            @NonNull SamsungPurchaseIncompleteType incomplete);
}
