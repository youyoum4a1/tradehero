package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.billing.ProductDetail;
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
    public static final int BG_COLOR_DISABLED_RES_ID = R.color.gray_2;
    public static final int BG_COLOR_ENABLED_RES_ID = R.color.gray_3;

    protected RadioButton hintSelected;
    protected View priceAndText;
    protected TextView skuPrice;
    protected TextView deliverableText;

    protected boolean selected;
    protected ProductDetailType skuDetails;

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
        initViews();
    }

    protected void initViews()
    {
        hintSelected = (RadioButton) findViewById(R.id.selected_hint);
        priceAndText = findViewById(R.id.price_and_text);
        skuPrice = (TextView) findViewById(R.id.sku_price);
        deliverableText = (TextView) findViewById(R.id.text_deliverable);
    }

    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        displayHintEnabled();
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

    @Override public void display(ProductDetailType productDetail)
    {
        linkWith(productDetail, true);
    }

    public void linkWith(ProductDetailType productDetail, boolean andDisplay)
    {
        this.skuDetails = productDetail;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayHintEnabled();
        displayHintSelected();
        displayPrice();
        displayDeliverableText();
    }

    protected void displayHintEnabled()
    {
        setBackgroundColor(getResources().getColor(isEnabled() ? BG_COLOR_ENABLED_RES_ID : BG_COLOR_DISABLED_RES_ID));
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
                skuPrice.setText(skuDetails.getPriceText());
            }
        }
    }

    protected void displayDeliverableText()
    {
        if (deliverableText != null)
        {
            if (skuDetails != null)
            {
                deliverableText.setText(skuDetails.getDescription());
            }
        }
    }
}
