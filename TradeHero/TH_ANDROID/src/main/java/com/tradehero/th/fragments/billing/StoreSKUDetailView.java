package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THIABProductDetail;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 2:49 PM To change this template use File | Settings | File Templates. */
public class StoreSKUDetailView extends SKUDetailView<THIABProductDetail>
{
    public static final String TAG = StoreSKUDetailView.class.getSimpleName();

    protected ImageView icDeliverable;
    protected TextView furtherDescription;
    protected ImageView icRibbon;

    //<editor-fold desc="Constructors">
    public StoreSKUDetailView(Context context)
    {
        super(context);
    }

    public StoreSKUDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreSKUDetailView(Context context, AttributeSet attrs, int defStyle)
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
                icDeliverable.setImageResource(skuDetails.iconResId);
            }
        }
    }

    protected void displayFurtherDescription()
    {
        if (furtherDescription != null)
        {
            if (skuDetails != null)
            {
                furtherDescription.setVisibility(skuDetails.hasFurtherDetails ? VISIBLE : GONE);
                furtherDescription.setText(skuDetails.furtherDetailsResId);
            }
        }
    }

    protected void displayIcRibbon()
    {
        if (icRibbon != null)
        {
            if (skuDetails != null)
            {
                icRibbon.setVisibility(skuDetails.hasRibbon ? VISIBLE : GONE);
                icRibbon.setImageResource(skuDetails.iconRibbonResId);
            }
        }
    }
}
