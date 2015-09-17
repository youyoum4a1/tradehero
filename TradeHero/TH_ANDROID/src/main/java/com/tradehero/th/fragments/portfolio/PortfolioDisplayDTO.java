package com.tradehero.th.fragments.portfolio;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DateUtils;

public class PortfolioDisplayDTO implements DTO
{
    public final String portfolioTitle;
    public final int portfolioTitleColor;
    @ViewVisibilityValue public final int roiVisibility;
    public final CharSequence roiValue;
    public final String description;
    public final String sinceValue;
    @ViewVisibilityValue public final int sinceValueVisibility;
    @ViewVisibilityValue public final int chartVisibility;
    @Nullable public final OwnedPortfolioId ownedPortfolioId;

    public PortfolioDisplayDTO(Resources resources, CurrentUserId currentUserId, DisplayablePortfolioDTO dto)
    {
        this.portfolioTitle = DisplayablePortfolioUtil.getLongTitle(resources, dto);
        this.portfolioTitleColor = DisplayablePortfolioUtil.getLongTitleTextColor(resources, dto);

        if (dto.portfolioDTO != null && dto.portfolioDTO.roiSinceInception != null)
        {
            String since = dto.portfolioDTO.isDefault()
                    ? resources.getString(R.string.roi_since_inception_format)
                    : resources.getString(R.string.roi_since_format, DateUtils.getDisplayableDate(resources, dto.portfolioDTO.creationDate,
                            R.string.data_format_d_mmm_yyyy));
            this.roiValue = THSignedPercentage.builder(dto.portfolioDTO.roiSinceInception * 100)
                    .withSign()
                    .signTypePlusMinusAlways()
                    .withDefaultColor()
                    .boldSign()
                    .boldValue()
                    .build()
                    .createSpanned();
            this.roiVisibility = View.VISIBLE;
            this.sinceValue = since;
            this.sinceValueVisibility = View.VISIBLE;
            this.chartVisibility = View.VISIBLE;
        }
        else if (dto instanceof DummyFxDisplayablePortfolioDTO)
        {
            this.roiVisibility = View.GONE;
            this.roiValue = "";
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.chartVisibility = View.GONE;
        }
        else if (dto.portfolioDTO != null
                && dto.portfolioDTO.isWatchlist)
        {
            this.roiVisibility = View.GONE;
            this.roiValue = "";
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.chartVisibility = View.GONE;
        }
        else
        {
            this.roiVisibility = View.VISIBLE;
            this.roiValue = resources.getString(R.string.na);
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.chartVisibility = View.GONE;
        }

        this.description = DisplayablePortfolioUtil.getLongSubTitle(resources, currentUserId, dto);

        this.ownedPortfolioId = dto.ownedPortfolioId;
    }
}
