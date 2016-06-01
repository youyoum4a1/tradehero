package com.ayondo.academy.billing.amazon.inventory;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.inventory.BaseAmazonInventoryFetcherRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import java.util.List;

public class THBaseAmazonInventoryFetcherRx
    extends BaseAmazonInventoryFetcherRx<
                AmazonSKU,
                THAmazonProductDetail>
    implements THAmazonInventoryFetcherRx
{
    //<editor-fold desc="Constructors">
    public THBaseAmazonInventoryFetcherRx(
            int request,
            @NonNull List<AmazonSKU> productIdentifiers,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, productIdentifiers, purchasingService);
    }
    //</editor-fold>

    @NonNull @Override protected AmazonSKU createAmazonSku(@NonNull String skuId)
    {
        return new AmazonSKU(skuId);
    }

    @NonNull @Override protected THAmazonProductDetail createAmazonProductDetail(@NonNull AmazonSKU amazonSKU, Product product)
    {
        return new THAmazonProductDetail(product);
    }
}
