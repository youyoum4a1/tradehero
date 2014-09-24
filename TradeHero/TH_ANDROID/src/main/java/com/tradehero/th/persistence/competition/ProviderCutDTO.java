package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ProviderCutDTO implements DTO
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
    public String textHexColor;
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
    public String wizardTitle;
    public String wizardImageUrl;
    public String ctaLocationTags;
    public String currencyDisplay;
    public String currencyISO;

    @Nullable public PortfolioId associatedPortfolioId;

    public Date startDateUtc;
    public Date endDateUtc;
    public String durationType;
    public String totalPrize;
    @Nullable public Boolean vip;

    @Nullable public ProviderSpecificKnowledgeDTO specificKnowledge;
    @Nullable public ProviderSpecificResourcesDTO specificResources;

    ProviderCutDTO(
            @NotNull ProviderDTO providerDTO,
            @NotNull PortfolioCompactCache portfolioCompactCache)
    {
        this.id = providerDTO.id;
        this.name = providerDTO.name;
        this.logoUrl = providerDTO.logoUrl;
        this.isUserEnrolled = providerDTO.isUserEnrolled;
        this.hexColor = providerDTO.hexColor;
        this.navigationLogoUrl = providerDTO.navigationLogoUrl;
        this.advertisements = providerDTO.advertisements;
        this.competitionScreenTitle = providerDTO.competitionScreenTitle;
        this.competitionScreenSubtitle = providerDTO.competitionScreenSubtitle;
        this.textHexColor = providerDTO.textHexColor;
        this.joinedLogoUrl = providerDTO.joinedLogoUrl;
        this.secondaryHexColor = providerDTO.secondaryHexColor;
        this.providerSubtitle = providerDTO.providerSubtitle;
        this.joinedProviderSubtitle = providerDTO.joinedProviderSubtitle;
        this.singleRowHeightPoint = providerDTO.singleRowHeightPoint;
        this.multiRowHeightPoint = providerDTO.multiRowHeightPoint;
        this.singleImageUrl = providerDTO.singleImageUrl;
        this.singleSelectedImageUrl = providerDTO.singleSelectedImageUrl;
        this.multiImageUrl = providerDTO.multiImageUrl;
        this.multiSelectedImageUrl = providerDTO.multiSelectedImageUrl;
        this.singleJoinedImageUrl = providerDTO.singleJoinedImageUrl;
        this.singleJoinedSelectedImageUrl = providerDTO.singleJoinedSelectedImageUrl;
        this.multiJoinedImageUrl = providerDTO.multiJoinedImageUrl;
        this.multiJoinedSelectedImageUrl = providerDTO.multiJoinedSelectedImageUrl;
        this.tradeButtonImageUrl = providerDTO.tradeButtonImageUrl;
        this.tradeButtonSelectedImageUrl = providerDTO.tradeButtonSelectedImageUrl;
        this.tileImageUrl = providerDTO.tileImageUrl;
        this.tileJoinedImageUrl = providerDTO.tileJoinedImageUrl;
        this.timerImageUrl = providerDTO.timerImageUrl;
        this.volumeRestrictionEnabled = providerDTO.volumeRestrictionEnabled;
        this.tradeRestrictionStartHourUtc = providerDTO.tradeRestrictionStartHourUtc;
        this.tradeRestrictionEndHourUtc = providerDTO.tradeRestrictionEndHourUtc;
        this.appiTunesUrl = providerDTO.appiTunesUrl;
        this.iosAppSecurityPageUrlTemplate = providerDTO.iosAppSecurityPageUrlTemplate;
        this.helpVideoText = providerDTO.helpVideoText;
        this.ruleText = providerDTO.ruleText;
        this.hasHelpVideo = providerDTO.hasHelpVideo;
        this.wizardUrl = providerDTO.wizardUrl;
        this.wizardTitle = providerDTO.wizardTitle;
        this.wizardImageUrl = providerDTO.wizardImageUrl;
        this.ctaLocationTags = providerDTO.ctaLocationTags;
        this.currencyDisplay = providerDTO.currencyDisplay;
        this.currencyISO = providerDTO.currencyISO;

        PortfolioCompactDTO associatedPortfolio = providerDTO.associatedPortfolio;
        if (associatedPortfolio != null)
        {
            this.associatedPortfolioId = associatedPortfolio.getPortfolioId();
            portfolioCompactCache.put(associatedPortfolioId, associatedPortfolio);
        }
        else
        {
            this.associatedPortfolioId = null;
        }

        this.startDateUtc = providerDTO.startDateUtc;
        this.endDateUtc = providerDTO.endDateUtc;
        this.durationType = providerDTO.durationType;
        this.totalPrize = providerDTO.totalPrize;
        this.vip = providerDTO.vip;

        this.specificKnowledge = providerDTO.specificKnowledge;
        this.specificResources = providerDTO.specificResources;
    }

    @Nullable ProviderDTO create(@NotNull PortfolioCompactCache portfolioCompactCache)
    {
        return populate(new ProviderDTO(), portfolioCompactCache);
    }



    @Nullable <T extends ProviderDTO> T populate(
            @NotNull T empty,
            @NotNull PortfolioCompactCache portfolioCompactCache)
    {
        if (this.associatedPortfolioId != null)
        {
            PortfolioCompactDTO associatedPortfolio = portfolioCompactCache.get(this.associatedPortfolioId);
            if (associatedPortfolio == null)
            {
                return null;
            }
            empty.associatedPortfolio = associatedPortfolio;
        }

        empty.id = this.id;
        empty.name = this.name;
        empty.logoUrl = this.logoUrl;
        empty.isUserEnrolled = this.isUserEnrolled;
        empty.hexColor = this.hexColor;
        empty.navigationLogoUrl = this.navigationLogoUrl;
        empty.advertisements = this.advertisements;
        empty.competitionScreenTitle = this.competitionScreenTitle;
        empty.competitionScreenSubtitle = this.competitionScreenSubtitle;
        empty.textHexColor = this.textHexColor;
        empty.joinedLogoUrl = this.joinedLogoUrl;
        empty.secondaryHexColor = this.secondaryHexColor;
        empty.providerSubtitle = this.providerSubtitle;
        empty.joinedProviderSubtitle = this.joinedProviderSubtitle;
        empty.singleRowHeightPoint = this.singleRowHeightPoint;
        empty.multiRowHeightPoint = this.multiRowHeightPoint;
        empty.singleImageUrl = this.singleImageUrl;
        empty.singleSelectedImageUrl = this.singleSelectedImageUrl;
        empty.multiImageUrl = this.multiImageUrl;
        empty.multiSelectedImageUrl = this.multiSelectedImageUrl;
        empty.singleJoinedImageUrl = this.singleJoinedImageUrl;
        empty.singleJoinedSelectedImageUrl = this.singleJoinedSelectedImageUrl;
        empty.multiJoinedImageUrl = this.multiJoinedImageUrl;
        empty.multiJoinedSelectedImageUrl = this.multiJoinedSelectedImageUrl;
        empty.tradeButtonImageUrl = this.tradeButtonImageUrl;
        empty.tradeButtonSelectedImageUrl = this.tradeButtonSelectedImageUrl;
        empty.tileImageUrl = this.tileImageUrl;
        empty.tileJoinedImageUrl = this.tileJoinedImageUrl;
        empty.timerImageUrl = this.timerImageUrl;
        empty.volumeRestrictionEnabled = this.volumeRestrictionEnabled;
        empty.tradeRestrictionStartHourUtc = this.tradeRestrictionStartHourUtc;
        empty.tradeRestrictionEndHourUtc = this.tradeRestrictionEndHourUtc;
        empty.appiTunesUrl = this.appiTunesUrl;
        empty.iosAppSecurityPageUrlTemplate = this.iosAppSecurityPageUrlTemplate;
        empty.helpVideoText = this.helpVideoText;
        empty.ruleText = this.ruleText;
        empty.hasHelpVideo = this.hasHelpVideo;
        empty.wizardUrl = this.wizardUrl;
        empty.wizardTitle = this.wizardTitle;
        empty.wizardImageUrl = this.wizardImageUrl;
        empty.ctaLocationTags = this.ctaLocationTags;
        empty.currencyDisplay = this.currencyDisplay;
        empty.currencyISO = this.currencyISO;

        empty.startDateUtc = this.startDateUtc;
        empty.endDateUtc = this.endDateUtc;
        empty.durationType = this.durationType;
        empty.totalPrize = this.totalPrize;
        empty.vip = this.vip;

        empty.specificKnowledge = specificKnowledge;
        empty.specificResources = specificResources;

        return empty;
    }
}
