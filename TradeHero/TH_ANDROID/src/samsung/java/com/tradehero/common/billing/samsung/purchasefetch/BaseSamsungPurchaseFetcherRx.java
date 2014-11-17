package com.tradehero.common.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.SamsungInboxOperator;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
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
    public static final int FIRST_ITEM_NUM = 1;
    public static final String FIRST_DATE = "20140101";

    protected final int startNum;
    protected final int endNum;
    @NonNull protected final String startDate;
    @NonNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            int startNum,
            int endNum,
            @NonNull String startDate,
            @NonNull String groupId)
    {
        super(requestCode, context, mode);
        this.startNum = startNum;
        this.endNum = endNum;
        this.startDate = startDate;
        this.groupId = groupId;
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseFetchResult<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType>> get()
    {
        return replayObservable;
    }

    protected void fetchPurchases()
    {
        Observable.create(new SamsungInboxOperator(context, mode, startNum, endNum, startDate, groupId))
                .map(this::createIncompletePurchase)
                .map(this::mergeWithSaved)
                .map(purchase -> new PurchaseFetchResult<>(getRequestCode(), purchase))
                .subscribe(subject);
    }

    @NonNull abstract protected SamsungPurchaseIncompleteType createIncompletePurchase(@NonNull InboxVo inboxVo);

    @NonNull abstract protected SamsungPurchaseType mergeWithSaved(@NonNull SamsungPurchaseIncompleteType incomplete);
}
