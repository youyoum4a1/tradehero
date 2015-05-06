package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.fragments.onboarding.OnBoardSelectableViewLinear;
import com.tradehero.th.models.number.THSignedMoney;
import javax.inject.Inject;

public class OnBoardExchangeItemView extends OnBoardSelectableViewLinear<ExchangeCompactDTO, SelectableDTO<ExchangeCompactDTO>>
{
    @DrawableRes private static final int DEFAULT_EXCHANGE_LOGO = R.drawable.accounts_glyph_name_default;

    @Inject Picasso picasso;

    @InjectView(android.R.id.icon) ImageView flagImage;
    @InjectView(android.R.id.icon1) ImageView logoImage;
    @InjectView(android.R.id.text1) TextView shortNameView;
    @InjectView(android.R.id.text2) TextView nameView;
    @InjectView(R.id.market_cap) TextView marketCapView;
    View marketCapSliderView;
    @InjectView(R.id.top_stock_list) TopStockListView topStockListView;

    //<editor-fold desc="Constructors">
    public OnBoardExchangeItemView(Context context)
    {
        super(context);
    }

    public OnBoardExchangeItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardExchangeItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
        }
        ButterKnife.reset(this);
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
                        THSignedMoney.builder(dto.getSumMarketCap()).with000Suffix().build().toString()));
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
