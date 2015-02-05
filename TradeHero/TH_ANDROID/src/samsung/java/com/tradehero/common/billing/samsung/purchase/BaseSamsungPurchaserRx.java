package com.tradehero.common.billing.samsung.purchase;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.rx.SamsungPaymentOperator;
import rx.Observable;

abstract public class BaseSamsungPurchaserRx<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BaseSamsungActorRx<PurchaseResult<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>>
        implements SamsungPurchaserRx<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    @NonNull protected final SamsungPurchaseOrderType purchaseOrder;
    protected final boolean showSucessDialog;

    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaserRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull SamsungPurchaseOrderType purchaseOrder,
            boolean showSucessDialog)
    {
        super(requestCode, context, mode);
        this.purchaseOrder = purchaseOrder;
        this.showSucessDialog = showSucessDialog;
    }
    //</editor-fold>

    @NonNull @Override public SamsungPurchaseOrderType getPurchaseOrder()
    {
        return purchaseOrder;
    }

    @NonNull @Override public Observable<PurchaseResult<
            SamsungSKUType,
            SamsungPurchaseOrderType,
            SamsungOrderIdType,
            SamsungPurchaseType>> get()
    {
        SamsungSKUType sku = purchaseOrder.getProductIdentifier();
        return Observable.create(
                new SamsungPaymentOperator(
                        context,
                        mode,
                        sku.groupId,
                        sku.itemId,
                        showSucessDialog))
                .map(this::createSamsungPurchase)
                .map(purchase -> new PurchaseResult<>(getRequestCode(), purchaseOrder, purchase));
    }

    @NonNull abstract protected SamsungPurchaseType createSamsungPurchase(@NonNull PurchaseVo purchaseVo);
}
