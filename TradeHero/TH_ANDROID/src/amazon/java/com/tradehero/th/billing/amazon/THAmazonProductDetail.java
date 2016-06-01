package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.R;
import com.ayondo.academy.billing.ProductIdentifierDomain;
import com.ayondo.academy.billing.THProductDetail;

public class THAmazonProductDetail extends AmazonProductDetail<AmazonSKU>
    implements THProductDetail<AmazonSKU>
{
    int iconResId;
    boolean hasFurtherDetails = false;
    int furtherDetailsResId = R.string.na;
    ProductIdentifierDomain domain;
    public int displayOrder;
    public int storeDescriptionResId;
    public int storeTitleResId;

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

    @Override public int getDisplayOrder()
    {
        return displayOrder;
    }

    @Override public int getStoreTitleResId()
    {
        return storeTitleResId;
    }

    @Override public int getStoreDescriptionResId()
    {
        return storeDescriptionResId;
    }
}
