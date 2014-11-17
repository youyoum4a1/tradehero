package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonProductIdentifierFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;

public class THBaseAmazonProductIdentifierFetcher
    extends BaseAmazonProductIdentifierFetcher<
            AmazonSKUListKey,
                AmazonSKU,
            AmazonSKUList,
            AmazonException>
    implements THAmazonProductIdentifierFetcher
{
    //<editor-fold desc="Constructors">
    public THBaseAmazonProductIdentifierFetcher(
            int requestCode,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(requestCode, purchasingService);
    }
    //</editor-fold>

    public static AmazonSKUList getAllSkus()
    {
        AmazonSKUList list = new AmazonSKUList();
        list.add(new AmazonSKU(THAmazonConstants.EXTRA_CASH_T0_KEY));
        list.add(new AmazonSKU(THAmazonConstants.EXTRA_CASH_T1_KEY));
        list.add(new AmazonSKU(THAmazonConstants.EXTRA_CASH_T2_KEY));
        list.add(new AmazonSKU(THAmazonConstants.CREDIT_1));
        list.add(new AmazonSKU(THAmazonConstants.CREDIT_10));
        list.add(new AmazonSKU(THAmazonConstants.CREDIT_20));
        list.add(new AmazonSKU(THAmazonConstants.RESET_PORTFOLIO_0));
        list.add(new AmazonSKU(THAmazonConstants.ALERT_1));
        list.add(new AmazonSKU(THAmazonConstants.ALERT_5));
        list.add(new AmazonSKU(THAmazonConstants.ALERT_UNLIMITED));
        return list;
    }

    @Override protected void populate(AmazonSKUList list, ProductType productType)
    {
        switch (productType)
        {
            case CONSUMABLE:
                list.add(createAmazonSku(THAmazonConstants.EXTRA_CASH_T0_KEY));
                list.add(createAmazonSku(THAmazonConstants.EXTRA_CASH_T1_KEY));
                list.add(createAmazonSku(THAmazonConstants.EXTRA_CASH_T2_KEY));
                list.add(createAmazonSku(THAmazonConstants.CREDIT_1));
                list.add(createAmazonSku(THAmazonConstants.CREDIT_10));
                list.add(createAmazonSku(THAmazonConstants.CREDIT_20));
                list.add(createAmazonSku(THAmazonConstants.RESET_PORTFOLIO_0));
                break;
            case ENTITLED:
                break;
            case SUBSCRIPTION:
                list.add(createAmazonSku(THAmazonConstants.ALERT_1));
                list.add(createAmazonSku(THAmazonConstants.ALERT_5));
                list.add(createAmazonSku(THAmazonConstants.ALERT_UNLIMITED));
                break;
        }
    }

    @Override protected AmazonSKUListKey createAmazonListKey(ProductType productType)
    {
        return new AmazonSKUListKey(productType);
    }

    protected AmazonSKU createAmazonSku(String skuId)
    {
        return new AmazonSKU(skuId);
    }

    @Override protected AmazonSKUList createAmazonSKUList()
    {
        return new AmazonSKUList();
    }
}
