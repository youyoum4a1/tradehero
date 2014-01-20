package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:12 PM Copyright (c) TradeHero */
public class ProviderDTO implements DTO
{
    public int id;
    public String name;
    public String logoUrl;
    public boolean isUserEnrolled;
    public String hexColor;
    public String navigationLogoUrl;
    public List<AdDTO> advertisements;
    public String competitionScreenTitle;
    public String competitionScreenSubtitle;
    public String joinedLogoUrl;
    public String secondaryHexColor;
    public String providerSubtitle;
    public String joinedProviderSubtitle;
    public double singleRowHeightPoint;
    public double multiRowHeightPoint;
    public String singleImageUrl;
    public String singleSelectedImageUrl;
    public String multiImageUrl;
    public String multiSelectedImageUrl;
    public String singleJoinedImageUrl;
    public String singleJoinedSelectedImageUrl;
    public String multiJoinedImageUrl;
    public String multiJoinedSelectedImageUrl;
    public String tradeButtonImageUrl;
    public String tradeButtonSelectedImageUrl;
    public String tileImageUrl;
    public String tileJoinedImageUrl;
    public String timerImageUrl;
    public boolean volumeRestrictionEnabled;
    public Date tradeRestrictionStartHourUtc;
    public Date tradeRestrictionEndHourUtc;
    public String appiTunesUrl;
    public String iosAppSecurityPageUrlTemplate;
    public String helpVideoText;
    public String ruleText;
    public boolean hasHelpVideo;
    public String wizardUrl;

    public PortfolioCompactDTO associatedPortfolio;

    /**
     * Creates the id that identifies this DTO.
     * @return
     */
    public ProviderId getProviderId()
    {
        return new ProviderId(id);
    }

    public String getStatusSingleImageUrl(boolean isSelected)
    {
        return isSelected ? getStatusSingleSelectedImageUrl() : getStatusSingleImageUrl();
    }

    public String getStatusSingleImageUrl()
    {
        return isUserEnrolled ? singleJoinedImageUrl : singleImageUrl;
    }

    public String getStatusSingleSelectedImageUrl()
    {
        return isUserEnrolled ? singleJoinedSelectedImageUrl : singleSelectedImageUrl;
    }

    public String getStatusMultiImageUrl(boolean isSelected)
    {
        return isSelected ? getStatusMultiSelectedImageUrl() : getStatusMultiImageUrl();
    }

    public String getStatusMultiImageUrl()
    {
        return isUserEnrolled ? multiJoinedImageUrl : multiImageUrl;
    }

    public String getStatusMultiSelectedImageUrl()
    {
        return isUserEnrolled ? multiJoinedSelectedImageUrl : multiSelectedImageUrl;
    }

    public String getStatusTileImageUrl()
    {
        return isUserEnrolled ? tileJoinedImageUrl : tileImageUrl;
    }

    public String getTradeButtonImageUrl(boolean isSelected)
    {
        return isSelected ? tradeButtonSelectedImageUrl : tradeButtonImageUrl;
    }

    public static List<ProviderId> getProviderIds(List<ProviderDTO> providerDTOs)
    {
        if (providerDTOs == null)
        {
            return null;
        }

        List<ProviderId> providerIds = new ArrayList<>();

        for (ProviderDTO providerDTO: providerDTOs)
        {
            providerIds.add(providerDTO.getProviderId());
        }

        return providerIds;
    }
}