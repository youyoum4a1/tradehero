package com.androidth.general.fragments.security;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOUtil;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.key.FxPairSecurityId;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedFXRate;

public class FXItemView extends RelativeLayout implements DTOView<SecurityCompactDTO>
{
    @Bind(R.id.fx_pair_name) TextView fxPairName;
    @Bind(R.id.flags_container) protected FxFlagContainer flagsContainer;
    @Bind(R.id.fx_price_buy) TextView buyPrice;
    @Bind(R.id.fx_price_sell) TextView sellPrice;

    protected FxSecurityCompactDTO fxSecurityCompactDTO;
    private int mBlinkDuration;
    @ColorRes private int mDefaultTextColor;

    //<editor-fold desc="Constructors">
    public FXItemView(Context context)
    {
        super(context);
    }

    public FXItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FXItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    protected void init()
    {
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
        mDefaultTextColor = R.color.text_primary;
        mBlinkDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override public void display(final SecurityCompactDTO securityCompactDTO)
    {
        this.fxSecurityCompactDTO = (FxSecurityCompactDTO) securityCompactDTO;
        displayFlagContainer();
        displayStockName();
        displayPrice();
    }

    private void displayPrice()
    {
        if(fxSecurityCompactDTO.askPrice != null && fxSecurityCompactDTO.bidPrice != null)
        {
            int precision = SecurityCompactDTOUtil.getExpectedPrecision(fxSecurityCompactDTO);
            coloredText(buyPrice, fxSecurityCompactDTO.askPrice, fxSecurityCompactDTO.fxAskTextColorResId, precision);
            coloredText(sellPrice, fxSecurityCompactDTO.bidPrice, fxSecurityCompactDTO.fxBidTextColorResId, precision);
        }
    }

    private void coloredText(TextView textView, double value, @ColorRes int colorResId, int precision)
    {
        THSignedFXRate.builder(value)
                .enhanceTo((int) (textView.getTextSize() + 15))
                .enhanceWithColor(colorResId)
                .expectedPrecision(precision)
                .relevantDigitCount(SecurityCompactDTOUtil.DEFAULT_RELEVANT_DIGITS)
                .build()
                .into(textView);
        if (colorResId != mDefaultTextColor)
        {
            //Adds a blinking animation.
            textView.setAlpha(0.1f);
            textView.animate().alpha(1).setDuration(mBlinkDuration).setInterpolator(new AccelerateInterpolator(3)).start();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockName();
        displayFlagContainer();
        displayPrice();
    }

    public void displayStockName()
    {
        if (fxPairName != null)
        {
            if (fxSecurityCompactDTO != null)
            {
                FxPairSecurityId pair = fxSecurityCompactDTO.getFxPair();
                fxPairName.setText(String.format("%s/%s", pair.left, pair.right));
            }
            else
            {
                fxPairName.setText(R.string.na);
            }
        }
    }

    public void displayFlagContainer()
    {
        if (flagsContainer != null)
        {
            if (fxSecurityCompactDTO != null)
            {
                flagsContainer.display(fxSecurityCompactDTO.getFxPair());
            }
        }
    }
    //</editor-fold>
}
