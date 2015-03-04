package com.tradehero.common.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
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
import rx.functions.Func1;

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
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseFetchResult<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType>> get()
    {
        return new SamsungInboxOperatorZip(context, mode, getInboxListQueryGroups())
                .getInboxItems()
                .flatMap(new Func1<Pair<InboxListQueryGroup, Observable<InboxVo>>, Observable<? extends SamsungPurchaseIncompleteType>>()
                {
                    @Override public Observable<? extends SamsungPurchaseIncompleteType> call(
                            final Pair<InboxListQueryGroup, Observable<InboxVo>> pair)
                    {
                        return pair.second
                                .map(new Func1<InboxVo, SamsungPurchaseIncompleteType>()
                                {
                                    @Override public SamsungPurchaseIncompleteType call(InboxVo inboxVo)
                                    {
                                        return BaseSamsungPurchaseFetcherRx.this.createIncompletePurchase(pair.first, inboxVo);
                                    }
                                });
                    }
                })
                .map(new Func1<SamsungPurchaseIncompleteType, PurchaseFetchResult<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType>>()
                {
                    @Override public PurchaseFetchResult<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType> call(SamsungPurchaseIncompleteType incomplete)
                    {
                        SamsungPurchaseType purchase = BaseSamsungPurchaseFetcherRx.this.mergeWithSaved(incomplete);
                        return new PurchaseFetchResult<>(BaseSamsungPurchaseFetcherRx.this.getRequestCode(), purchase);
                    }
                });
    }

    @NonNull abstract protected List<InboxListQueryGroup> getInboxListQueryGroups();

    @NonNull abstract protected SamsungPurchaseIncompleteType createIncompletePurchase(
            @NonNull InboxListQueryGroup queryGroup,
            @NonNull InboxVo inboxVo);

    @NonNull abstract protected SamsungPurchaseType mergeWithSaved(
            @NonNull SamsungPurchaseIncompleteType incomplete);
}
