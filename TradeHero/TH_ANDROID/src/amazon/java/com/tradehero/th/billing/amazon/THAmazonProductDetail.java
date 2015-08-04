package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;

public class THAmazonProductDetail extends AmazonProductDetail<AmazonSKU>
    implements THProductDetail<AmazonSKU>
{
    int iconResId;
    boolean hasFurtherDetails = false;
    int furtherDetailsResId = R.string.na;
    ProductIdentifierDomain domain;

    //<editor-fold desc="Constructors">
    public THAmazonProductDetail(@NonNull Product product)
    {
        super(product);
    }
    //</editor-fold>

    @NonNull @Override public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(product.getSku());
    }

    @Override public int getIconResId()
    {
        return iconResId;
    }

    @Override public boolean getHasFurtherDetails()
    {
        return hasFurtherDetails;
    }

    @Override public int getFurtherDetailsResId()
    {
        return furtherDetailsResId;
    }

    @Override public ProductIdentifierDomain getDomain()
    {
        return domain;
    }

    @Override @Nullable public Double getPrice()
    {
        return null;
    }

    @Override public String getPriceText()
    {
        return product.getPrice();
    }

    @Override @Nullable public String getDescription()
    {
        return product.getTitle();
    }
}
