package com.tradehero.th.widget.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.billing.googleplay.SKUDetails;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.billing.googleplay.THSKUDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 2:49 PM To change this template use File | Settings | File Templates. */
public class StoreSKUDetailView extends RelativeLayout implements DTOView<THSKUDetails>
{
    public static final String TAG = StoreSKUDetailView.class.getSimpleName();

    private RadioButton hintSelected;
    private ImageView icDeliverable;
    private View priceAndText;
    private TextView skuPrice;
    private TextView deliverableText;
    private TextView furtherDescription;
    private ImageView icRibbon;

    private boolean selected;
    private THSKUDetails skuDetails;

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

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        hintSelected = (RadioButton) findViewById(R.id.selected_hint);
        icDeliverable = (ImageView) findViewById(R.id.ic_deliverable);
        priceAndText = findViewById(R.id.price_and_text);
        skuPrice = (TextView) findViewById(R.id.sku_price);
        deliverableText = (TextView) findViewById(R.id.text_deliverable);
        furtherDescription = (TextView) findViewById(R.id.further_description);
        icRibbon = (ImageView) findViewById(R.id.ic_ribbon);
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

    @Override public void display(THSKUDetails skuDetails)
    {
        linkWith(skuDetails, true);
    }

    public void linkWith(THSKUDetails skuDetails, boolean andDisplay)
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
        displayIcDeliverable();
        displayPrice();
        displayDeliverableText();
        displayFurtherDescription();
        displayIcRibbon();
    }

    private void displayHintSelected()
    {
        if (hintSelected != null)
        {
            hintSelected.setChecked(selected);
        }
    }

    private void displayIcDeliverable()
    {
        if (icDeliverable != null)
        {
            if (skuDetails != null)
            {
                icDeliverable.setImageResource(skuDetails.iconResId);
            }
        }
    }

    private void displayPrice()
    {
        if (skuPrice != null)
        {
            if (skuDetails != null)
            {
                skuPrice.setText(skuDetails.price);
            }
        }
    }

    private void displayDeliverableText()
    {
        if (deliverableText != null)
        {
            if (skuDetails != null)
            {
                deliverableText.setText(skuDetails.description);
            }
        }
    }

    private void displayFurtherDescription()
    {
        if (furtherDescription != null)
        {
            if (skuDetails != null)
            {
                furtherDescription.setVisibility(skuDetails.hasFurtherDetails ? View.VISIBLE : View.GONE);
                furtherDescription.setText(skuDetails.furtherDetailsResId);
            }
        }
    }

    private void displayIcRibbon()
    {
        if (icRibbon != null)
        {
            if (skuDetails != null)
            {
                icRibbon.setVisibility(skuDetails.hasRibbon ? View.VISIBLE : View.GONE);
                icRibbon.setImageResource(skuDetails.iconRibbonResId);
            }
        }
    }
}
