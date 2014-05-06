package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.exception.SamsungException;


abstract public class BaseSamsungPurchaser<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
    implements SamsungPurchaser<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
    private int activityRequestCode;
    private SamsungPurchaseOrderType purchaseOrder;
    private OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> purchaseFinishedListener;
    private SamsungIapHelper mIapHelper;

    public BaseSamsungPurchaser(Context context, int mode)
    {
        super();
        mIapHelper = SamsungIapHelper.getInstance(context, mode);
    }

    @Override public int getRequestCode()
    {
        return activityRequestCode;
    }

    @Override public OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> getPurchaseFinishedListener()
    {
        return this.purchaseFinishedListener;
    }

    @Override public void setPurchaseFinishedListener(OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> purchaseFinishedListener)
    {
        this.purchaseFinishedListener = purchaseFinishedListener;
    }

    @Override public void purchase(int requestCode, SamsungPurchaseOrderType purchaseOrder)
    {
        this.activityRequestCode = requestCode;
        this.purchaseOrder = purchaseOrder;
        SamsungSKUType sku = purchaseOrder.getProductIdentifier();
        mIapHelper.startPayment(sku.groupId, sku.itemId, true, this);
    }

    @Override public void onPayment(ErrorVo errorVo, PurchaseVo purchaseVo)
    {
        if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
        {
            notifyPurchaseFinished(createSamsungPurchase(purchaseVo));
        }
        else
        {
            notifyPurchaseFailed(createSamsungException(errorVo));
        }
    }

    abstract SamsungPurchaseType createSamsungPurchase(PurchaseVo purchaseVo);
    abstract SamsungExceptionType createSamsungException(ErrorVo errorVo);

    protected void notifyPurchaseFinished(SamsungPurchaseType purchase)
    {
        OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> listenerCopy = purchaseFinishedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onPurchaseFinished(activityRequestCode, purchaseOrder, purchase);
        }
    }

    protected void notifyPurchaseFailed(SamsungExceptionType exception)
    {
        OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> listenerCopy = purchaseFinishedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onPurchaseFailed(activityRequestCode, purchaseOrder, exception);
        }
    }
}
