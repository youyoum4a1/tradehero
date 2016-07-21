package com.androidth.general.fragments.onboarding.sector;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.squareup.picasso.Picasso;
import com.androidth.general.common.api.SelectableDTO;
import com.androidth.general.R;
import com.androidth.general.api.market.SectorDTO;
import com.androidth.general.fragments.onboarding.OnBoardSelectableViewLinear;
import com.androidth.general.fragments.onboarding.exchange.TopStockListView;
import javax.inject.Inject;

public class OnBoardSectorItemView extends OnBoardSelectableViewLinear<SectorDTO, SelectableDTO<SectorDTO>>
{
    @DrawableRes private static final int DEFAULT_SECTOR_LOGO = R.drawable.accounts_glyph_name_default;

    @Inject Picasso picasso;

    @BindView(android.R.id.icon1) ImageView logoImage;
    @BindView(android.R.id.text1) TextView shortNameView;
    @BindView(R.id.top_stock_list) TopStockListView topStockListView;

    //<editor-fold desc="Constructors">
    public OnBoardSectorItemView(Context context)
    {
        super(context);
    }

    public OnBoardSectorItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardSectorItemView(Context context, AttributeSet attrs, int defStyleAttr)
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
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull SelectableDTO<SectorDTO> dto)
    {
        super.display(dto);
        display(dto.value);
    }

    protected void display(@Nullable SectorDTO dto)
    {
        if (logoImage != null)
        {
            picasso.cancelRequest(logoImage);
            if (dto == null || dto.imageUrl == null)
            {
                logoImage.setImageResource(DEFAULT_SECTOR_LOGO);
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
