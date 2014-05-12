package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.billing.THProductDetail;


public class StoreProductDetailView<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends ProductDetailView<ProductIdentifierType, ProductDetailType>
{
    protected ImageView icDeliverable;
    protected TextView furtherDescription;
    protected ImageView icRibbon;

    //<editor-fold desc="Constructors">
    public StoreProductDetailView(Context context)
    {
        super(context);
    }

    public StoreProductDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreProductDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void initViews()
    {
        super.initViews();
        icDeliverable = (ImageView) findViewById(R.id.ic_deliverable);
        furtherDescription = (TextView) findViewById(R.id.further_description);
        icRibbon = (ImageView) findViewById(R.id.ic_ribbon);
    }

    @Override public void display()
    {
        super.display();
        displayIcDeliverable();
        displayFurtherDescription();
        displayIcRibbon();
    }

    protected void displayIcDeliverable()
    {
        if (icDeliverable != null)
        {
            if (skuDetails != null)
            {
                icDeliverable.setImageResource(skuDetails.getIconResId());
            }
        }
    }

    protected void displayFurtherDescription()
    {
        if (furtherDescription != null)
        {
            if (skuDetails != null)
            {
                furtherDescription.setVisibility(skuDetails.getHasFurtherDetails() ? VISIBLE : GONE);
                furtherDescription.setText(skuDetails.getFurtherDetailsResId());
            }
        }
    }

    protected void displayIcRibbon()
    {
        if (icRibbon != null)
        {
            if (skuDetails != null)
            {
                icRibbon.setVisibility(skuDetails.getHasRibbon() ? VISIBLE : GONE);
                icRibbon.setImageResource(skuDetails.getIconRibbonResId());
            }
        }
    }
}
