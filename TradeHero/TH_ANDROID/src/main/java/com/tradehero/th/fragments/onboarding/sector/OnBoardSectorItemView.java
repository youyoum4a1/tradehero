package com.tradehero.th.fragments.onboarding.sector;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.SectorDTO;
import com.tradehero.th.api.market.SecuritySuperCompactDTOList;
import com.tradehero.th.fragments.onboarding.exchange.TopStockListView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import javax.inject.Inject;

public class OnBoardSectorItemView extends LinearLayout
        implements DTOView<SelectableSectorDTO>
{
    @DrawableRes private static final int DEFAULT_EXCHANGE_LOGO = R.drawable.accounts_glyph_name_default;

    @Inject Picasso picasso;

    private final float alphaUnSelected;
    @InjectView(android.R.id.icon1) ImageView logoImage;
    @InjectView(android.R.id.icon2) View selectedView;
    @InjectView(android.R.id.text1) TextView shortNameView;
    @InjectView(R.id.market_cap) TextView marketCapView;
    View marketCapSliderView;
    @InjectView(android.R.id.content) TopStockListView topStockListView;

    //<editor-fold desc="Constructors">
    public OnBoardSectorItemView(Context context)
    {
        super(context);
        alphaUnSelected = 1f;
    }

    public OnBoardSectorItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        alphaUnSelected = getAlpha(context, attrs);
    }

    public OnBoardSectorItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        alphaUnSelected = getAlpha(context, attrs);
    }
    //</editor-fold>

    static float getAlpha(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnBoardExchangeItemView);
        float region = a.getFloat(R.styleable.OnBoardExchangeItemView_alphaUnSelected, 1f);
        a.recycle();
        return region;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.inject(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
        }
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableSectorDTO dto)
    {
        display(dto.value);
        display(dto.value.topSecurities);

        if (selectedView != null)
        {
            selectedView.setVisibility(dto.selected ? VISIBLE : INVISIBLE);
        }
        setAlpha(dto.selected ? 1f : alphaUnSelected);
    }

    void display(@Nullable SectorDTO dto)
    {
        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
            if (dto == null || dto.logoUrl == null)
            {
                logoImage.setImageResource(DEFAULT_EXCHANGE_LOGO);
            }
            else
            {
                picasso.load(dto.logoUrl)
                        .into(logoImage);
            }
        }

        if (shortNameView != null)
        {
            if (dto == null)
            {
                shortNameView.setText(R.string.na);
            }
            else
            {
                shortNameView.setText(dto.name);
            }
        }

        if (marketCapView != null)
        {
            if (dto == null)
            {
                marketCapView.setText("");
            }
            else
            {
                marketCapView.setText(getResources().getString(
                        R.string.exchange_market_cap_abbreviated,
                        THSignedMoney.builder(dto.sumMarketCap).build().toString()));
            }
        }
    }

    void display(@Nullable SecuritySuperCompactDTOList topStocks)
    {
        if (topStockListView != null)
        {
            topStockListView.display(topStocks);
        }
    }
}
