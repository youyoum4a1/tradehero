package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.billing.googleplay.SKUDetails;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 2:49 PM To change this template use File | Settings | File Templates. */
public class SKUDetailView<SKUDetailsType extends SKUDetails>
        extends RelativeLayout implements DTOView<SKUDetailsType>
{
    public static final String TAG = SKUDetailView.class.getSimpleName();

    protected RadioButton hintSelected;
    protected View priceAndText;
    protected TextView skuPrice;
    protected TextView deliverableText;

    protected boolean selected;
    protected SKUDetailsType skuDetails;

    //<editor-fold desc="Constructors">
    public SKUDetailView(Context context)
    {
        super(context);
    }

    public SKUDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SKUDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    protected void initViews()
    {
        hintSelected = (RadioButton) findViewById(R.id.selected_hint);
        priceAndText = findViewById(R.id.price_and_text);
        skuPrice = (TextView) findViewById(R.id.sku_price);
        deliverableText = (TextView) findViewById(R.id.text_deliverable);
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
        displayHintSelected();
    }

    @Override public void display(SKUDetailsType skuDetails)
    {
        linkWith(skuDetails, true);
    }

    public void linkWith(SKUDetailsType skuDetails, boolean andDisplay)
    {
        this.skuDetails = skuDetails;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayHintSelected();
        displayPrice();
        displayDeliverableText();
    }

    protected void displayHintSelected()
    {
        if (hintSelected != null)
        {
            hintSelected.setChecked(selected);
        }
    }

    protected void displayPrice()
    {
        if (skuPrice != null)
        {
            if (skuDetails != null)
            {
                skuPrice.setText(skuDetails.price);
            }
        }
    }

    protected void displayDeliverableText()
    {
        if (deliverableText != null)
        {
            if (skuDetails != null)
            {
                deliverableText.setText(skuDetails.description);
            }
        }
    }
}
