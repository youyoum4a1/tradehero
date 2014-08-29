package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Price;
import com.amazon.device.iap.model.Product;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import com.tradehero.th.models.number.THSignedMoney;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THAmazonProductDetail extends AmazonProductDetail<AmazonSKU>
    implements THProductDetail<AmazonSKU>
{
    int iconResId;
    boolean hasFurtherDetails = false;
    int furtherDetailsResId = R.string.na;
    boolean hasRibbon = false;
    int iconRibbonResId = R.drawable.default_image;
    ProductIdentifierDomain domain;

    //<editor-fold desc="Constructors">
    public THAmazonProductDetail(@NotNull Product product)
    {
        super(product);
    }
    //</editor-fold>

    @NotNull @Override public AmazonSKU getProductIdentifier()
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

    @Override public boolean getHasRibbon()
    {
        return hasRibbon;
    }

    @Override public int getIconRibbonResId()
    {
        return iconRibbonResId;
    }

    @Override public ProductIdentifierDomain getDomain()
    {
        return domain;
    }

    @Override @Nullable public Double getPrice()
    {
        Price price = product.getPrice();
        if (price != null)
        {
            BigDecimal value = price.getValue();
            if (value != null)
            {
                return value.doubleValue();
            }
        }
        return null;
    }

    @Override public String getPriceText()
    {
        Price price = product.getPrice();
        Double priceValue = getPrice();
        if (price != null && priceValue != null)
        {
            return THSignedMoney.builder(priceValue)
                    .currency(price.getCurrency().getSymbol())
                    .build().toString();
        }
        return null;
    }

    @Override @Nullable public String getDescription()
    {
        return product.getDescription();
    }
}
