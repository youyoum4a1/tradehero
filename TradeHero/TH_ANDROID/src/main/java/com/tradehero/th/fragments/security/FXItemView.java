package com.tradehero.th.fragments.security;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import timber.log.Timber;

public class FXItemView extends RelativeLayout implements DTOView<FxSecurityCompactDTO>
{
    private static final int DECIMAL_PLACES_TO_BE_ENHANCED = 2;
    @InjectView(R.id.fx_pair_name) TextView fxPairName;
    @InjectView(R.id.flags_container) protected FxFlagContainer flagsContainer;
    @InjectView(R.id.fx_price_buy) TextView buyPrice;
    @InjectView(R.id.fx_price_sell) TextView sellPrice;
    @InjectView(R.id.ic_market_close) ImageView marketCloseIcon;
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

    @Override
    public void display(final FxSecurityCompactDTO securityCompactDTO)
    {
        linkWith(securityCompactDTO, true);
    }

    public void linkWith(FxSecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.fxSecurityCompactDTO = securityCompactDTO;
        displayFlagContainer();
        if (andDisplay)
        {
            displayStockName();
            displayMarketClose();
            displayPrice();
        }
    }

    private void displayPrice()
    {
        coloredText(buyPrice, String.valueOf(fxSecurityCompactDTO.askPrice), fxSecurityCompactDTO.fxAskTextColor);
        coloredText(sellPrice, String.valueOf(fxSecurityCompactDTO.bidPrice), fxSecurityCompactDTO.fxBidTextColor);
    }

    private void coloredText(TextView textView, String text, int color)
    {
        SpannableStringBuilder fontStyleBuilder = new SpannableStringBuilder(text);
        int length = text.length();

        fontStyleBuilder.setSpan(new AbsoluteSizeSpan((int) textView.getTextSize() + 10),
                length - DECIMAL_PLACES_TO_BE_ENHANCED, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (color != 0 && color != mDefaultTextColor)
        {
            fontStyleBuilder.setSpan(new ForegroundColorSpan(color),
                    length - DECIMAL_PLACES_TO_BE_ENHANCED, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        displayMarketClose();
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

    public void displayMarketClose()
    {
        if (fxSecurityCompactDTO == null)
        {
            // Nothing to do
        }
        else if (fxSecurityCompactDTO.marketOpen == null)
        {
            Timber.w("displayMarketClose marketOpen is null");
        }
        else if (fxSecurityCompactDTO.marketOpen)
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.GONE);
            }
        }
        else
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.VISIBLE);
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
