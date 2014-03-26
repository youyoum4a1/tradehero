package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/**
 * Created by xavier on 3/26/14.
 */
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

    @Override public void onPayment(ErrorVo _errorVO, PurchaseVo _purchaseVO)
    {
        if (_errorVO == null && _purchaseVO != null)
        {
            notifyPurchaseFinished(createSamsungPurchase(_purchaseVO));
        }
        else if (_errorVO != null && _purchaseVO == null)
        {
            notifyPurchaseFailed(createSamsungException(_errorVO));
        }
        else if (_errorVO == null)
        {
            throw new IllegalArgumentException("Both error and purchase are null");
        }
        else
        {
            throw new IllegalArgumentException(String.format("Not implemented Error:%s Purchase:%s", _errorVO.dump(), _purchaseVO.dump()));
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
