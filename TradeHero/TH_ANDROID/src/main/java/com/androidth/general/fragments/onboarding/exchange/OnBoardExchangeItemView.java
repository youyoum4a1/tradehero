package com.androidth.general.fragments.onboarding.exchange;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.common.graphics.WhiteToTransparentTransformation;
import com.androidth.general.R;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.market.ExchangeCompactDTO;
import com.androidth.general.fragments.onboarding.OnBoardSelectableViewLinear;
import javax.inject.Inject;

public class OnBoardExchangeItemView extends OnBoardSelectableViewLinear<ExchangeCompactDTO, SelectableDTO<ExchangeCompactDTO>>
{
    @DrawableRes private static final int DEFAULT_EXCHANGE_LOGO = R.drawable.accounts_glyph_name_default;

    @Inject Picasso picasso;

    @BindView(android.R.id.icon) ImageView flagImage;
    @BindView(android.R.id.icon1) ImageView logoImage;
    @BindView(android.R.id.text1) TextView nameView;
    @BindView(android.R.id.text2) TextView shortNameView;
    @BindView(R.id.top_stock_list) TopStockListView topStockListView;

    private Unbinder unbinder;
    @NonNull private final Transformation whiteTransformation;

    //<editor-fold desc="Constructors">
    public OnBoardExchangeItemView(Context context)
    {
        super(context);
        whiteTransformation = new WhiteToTransparentTransformation();
    }

    public OnBoardExchangeItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        whiteTransformation = new WhiteToTransparentTransformation();
    }

    public OnBoardExchangeItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        whiteTransformation = new WhiteToTransparentTransformation();
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
        }
        unbinder.unbind();
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableDTO<ExchangeCompactDTO> dto)
    {
        super.display(dto);
        display(dto.value);
    }

    protected void display(@Nullable ExchangeCompactDTO dto)
    {
        if (flagImage != null)
        {
            if (dto == null)
            {
                flagImage.setVisibility(INVISIBLE);
            }
            else
            {
                Country country = dto.getCountry();
                if (country != null)
                {
                    flagImage.setVisibility(VISIBLE);
                    flagImage.setImageResource(country.logoId);
                }
                else
                {
                    flagImage.setVisibility(INVISIBLE);
                }
            }
        }

        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
            if (dto == null || dto.imageUrl == null)
            {
                logoImage.setImageResource(DEFAULT_EXCHANGE_LOGO);
            }
            else
            {
                picasso.load(dto.imageUrl)
                        .transform(whiteTransformation)
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

        if (nameView != null)
        {
            if (dto == null)
            {
                nameView.setText("");
            }
            else
            {
                nameView.setText(dto.desc);
            }
        }

        if (topStockListView != null)
        {
            if (dto != null)
            {
                topStockListView.setVisibility(VISIBLE);
                topStockListView.display(dto.getTopSecurities());
            }
            else
            {
                topStockListView.setVisibility(GONE);
            }
        }
    }
}
