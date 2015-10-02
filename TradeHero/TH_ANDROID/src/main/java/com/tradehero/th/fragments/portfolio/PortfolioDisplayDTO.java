package com.tradehero.th.fragments.portfolio;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DateUtils;

public class PortfolioDisplayDTO implements DTO
{
    public final int portfolioId;
    public final String portfolioTitle;
    @ColorInt public final int portfolioTitleColor;
    @ViewVisibilityValue public final int roiVisibility;
    public final CharSequence roiValue;
    public final String description;
    public final String sinceValue;
    @ViewVisibilityValue public final int sinceValueVisibility;
    @Nullable public final OwnedPortfolioId ownedPortfolioId;
    public final boolean isWatchlist;
    @Nullable public final AssetClass assetClass;
    @Nullable public final Integer providerId;
    @Nullable public final CharSequence totalValue;
    @Nullable public final CharSequence marginLeft;
    public final boolean usesMargin;
    public final boolean isCompetition;
    public final String joinBanner;
    public boolean isVip;
    public final String cashMarginLabel;

    public PortfolioDisplayDTO(Resources resources, CurrentUserId currentUserId, DisplayablePortfolioDTO dto)
    {
        this.isCompetition = false;
        this.isVip = false;
        if (dto.portfolioDTO != null)
        {
            this.portfolioId = dto.portfolioDTO.id;
        }
        else
        {
            this.portfolioId = -1;
        }
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

            if (dto.portfolioDTO.assetClass != null && dto.portfolioDTO.assetClass.equals(AssetClass.FX))
            {
                this.totalValue = THSignedMoney.builder(dto.portfolioDTO.nav)
                        .currency(dto.portfolioDTO.getNiceCurrency())
                        .boldValue()
                        .build()
                        .createSpanned();
                this.marginLeft = THSignedMoney.builder(dto.portfolioDTO.marginAvailableRefCcy)
                        .currency(dto.portfolioDTO.getNiceCurrency())
                        .boldValue()
                        .build()
                        .createSpanned();
            }
            else
            {
                this.totalValue = THSignedMoney.builder(dto.portfolioDTO.totalValue)
                        .currency(dto.portfolioDTO.getNiceCurrency())
                        .boldValue()
                        .build()
                        .createSpanned();
                this.marginLeft = THSignedMoney.builder(dto.portfolioDTO.getUsableForTransactionRefCcy())
                        .currency(dto.portfolioDTO.getNiceCurrency())
                        .boldValue()
                        .build()
                        .createSpanned();
            }
        }
        else if (dto instanceof DummyFxDisplayablePortfolioDTO)
        {
            this.roiVisibility = View.GONE;
            this.roiValue = "";
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.totalValue = null;
            this.marginLeft = null;
        }
        else if (dto.portfolioDTO != null
                && dto.portfolioDTO.isWatchlist)
        {
            this.roiVisibility = View.GONE;
            this.roiValue = "";
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.totalValue = null;
            this.marginLeft = null;
        }
        else
        {
            this.roiVisibility = View.VISIBLE;
            this.roiValue = THSignedPercentage.builder(0).withOutSign().build().createSpanned();
            this.sinceValue = "";
            this.sinceValueVisibility = View.GONE;
            this.totalValue = null;
            this.marginLeft = null;
        }

        this.description = DisplayablePortfolioUtil.getLongSubTitle(resources, currentUserId, dto);
        this.ownedPortfolioId = dto.ownedPortfolioId;

        if (dto.portfolioDTO != null)
        {
            this.isWatchlist = dto.portfolioDTO.isWatchlist;
            this.assetClass = dto.portfolioDTO.assetClass;
            this.providerId = dto.portfolioDTO.providerId;
            this.usesMargin = dto.portfolioDTO.usesMargin();
        }
        else
        {
            this.isWatchlist = false;
            this.assetClass = null;
            this.providerId = null;
            this.usesMargin = false;
        }
        this.cashMarginLabel = resources.getString(usesMargin ? R.string.portfolio_header_margin_available_title : R.string.portfolio_header_cash_value);
        this.joinBanner = null;
    }

    public PortfolioDisplayDTO(Resources resources, ProviderDTO providerDTO)
    {
        this.isCompetition = true;
        this.isVip = providerDTO.vip != null ? providerDTO.vip : false;
        this.providerId = providerDTO.id;
        this.joinBanner = providerDTO.getStatusSingleImageUrl();
        this.portfolioTitle = providerDTO.name;
        this.portfolioId = -1;
        this.portfolioTitleColor = resources.getColor(R.color.text_primary);
        this.roiVisibility = View.GONE;
        this.roiValue = "";
        this.description = DateUtils.getDisplayableDate(resources, providerDTO.startDateUtc, providerDTO.endDateUtc);
        this.sinceValue = "";
        this.sinceValueVisibility = View.GONE;
        this.ownedPortfolioId = null;
        this.isWatchlist = false;
        this.assetClass = null;
        this.totalValue = null;
        this.marginLeft = null;
        this.usesMargin = false;
        this.cashMarginLabel = "";
    }

    public boolean isDefault()
    {
        return providerId == null && !isWatchlist;
    }
}
