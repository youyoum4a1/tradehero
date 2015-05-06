package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.billing.THProductDetail;

abstract public class ProductDetailView<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends RelativeLayout
        implements DTOView<ProductDetailType>
{
    @InjectView(R.id.sku_price) protected TextView skuPrice;
    @InjectView(R.id.text_deliverable) protected TextView deliverableText;

    //<editor-fold desc="Constructors">
    public ProductDetailView(Context context)
    {
        super(context);
    }

    public ProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : 0.5f);
    }

    @Override public void display(@NonNull ProductDetailType productDetail)
    {
        if (skuPrice != null)
        {
            skuPrice.setText(productDetail.getPriceText());
        }

        if (deliverableText != null)
        {
            deliverableText.setText(productDetail.getDescription());
        }
    }
}
