package com.tradehero.th.api.competition;

import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:12 PM Copyright (c) TradeHero */
public class ProviderDTO
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

    public PortfolioCompactDTO associatedPortfolio;

    public ProviderId getProviderId()
    {
        return new ProviderId(id);
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