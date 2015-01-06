package com.tradehero.th.fragments.security;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.inject.HierarchyInjector;

public class FXItemView extends RelativeLayout implements DTOView<FxSecurityCompactDTO>
{
    private static final int DECIMAL_PLACES_TO_BE_ENHANCED = 3;
    private static final int DECIMAL_PLACES_TO_BE_SKIPPED = 1;
    @InjectView(R.id.fx_pair_name) TextView fxPairName;
    @InjectView(R.id.flags_container) protected FxFlagContainer flagsContainer;
    @InjectView(R.id.fx_price_buy) TextView buyPrice;
    @InjectView(R.id.fx_price_sell) TextView sellPrice;
    protected FxSecurityCompactDTO fxSecurityCompactDTO;
    private int mBlinkDuration;
    private int mDefaultTextColor;

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
        ButterKnife.inject(this);
        mDefaultTextColor = getResources().getColor(R.color.text_primary);
        mBlinkDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override public void display(final FxSecurityCompactDTO securityCompactDTO)
    {
        linkWith(securityCompactDTO);
    }

    public void linkWith(FxSecurityCompactDTO securityCompactDTO)
    {
        this.fxSecurityCompactDTO = securityCompactDTO;
        displayFlagContainer();
        displayStockName();
        displayPrice();
    }

    private void displayPrice()
    {
        Pair<String, String> formattedPrices = new SecurityCompactDTOUtil().getFormattedAndPaddedAskBid(fxSecurityCompactDTO);
        coloredText(buyPrice, formattedPrices.first, fxSecurityCompactDTO.fxAskTextColor);
        coloredText(sellPrice, formattedPrices.second, fxSecurityCompactDTO.fxBidTextColor);
    }

    private void coloredText(TextView textView, String text, int color)
    {
        SpannableStringBuilder fontStyleBuilder = new SpannableStringBuilder(text);
        int length = text.length();

        fontStyleBuilder.setSpan(new AbsoluteSizeSpan((int) textView.getTextSize() + 15),
                length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length - DECIMAL_PLACES_TO_BE_SKIPPED, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        fontStyleBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length - DECIMAL_PLACES_TO_BE_SKIPPED, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (color != FxSecurityCompactDTO.DEFAULT_TEXT_COLOR && color != mDefaultTextColor)
        {
            fontStyleBuilder.setSpan(new ForegroundColorSpan(color),
                    length - Math.min(length, DECIMAL_PLACES_TO_BE_ENHANCED), length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //Adds a blinking animation
            textView.setAlpha(0.1f);
            textView.animate().alpha(1).setDuration(mBlinkDuration).setInterpolator(new AccelerateInterpolator(3)).start();
        }

        textView.setText(fontStyleBuilder);
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
