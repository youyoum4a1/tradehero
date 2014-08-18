package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ProviderCompactCutDTO implements DTO
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

    @Nullable public ProviderSpecificKnowledgeDTO specificKnowledge;
    @Nullable public ProviderSpecificResourcesDTO specificResources;

    ProviderCompactCutDTO(
            @NotNull ProviderCompactDTO providerCompactDTO,
            @NotNull PortfolioCompactCache portfolioCompactCache)
    {
        this.id = providerCompactDTO.id;
        this.name = providerCompactDTO.name;
        this.logoUrl = providerCompactDTO.logoUrl;
        this.isUserEnrolled = providerCompactDTO.isUserEnrolled;
        this.hexColor = providerCompactDTO.hexColor;
        this.navigationLogoUrl = providerCompactDTO.navigationLogoUrl;
        this.advertisements = providerCompactDTO.advertisements;
        this.competitionScreenTitle = providerCompactDTO.competitionScreenTitle;
        this.competitionScreenSubtitle = providerCompactDTO.competitionScreenSubtitle;
        this.textHexColor = providerCompactDTO.textHexColor;
        this.joinedLogoUrl = providerCompactDTO.joinedLogoUrl;
        this.secondaryHexColor = providerCompactDTO.secondaryHexColor;
        this.providerSubtitle = providerCompactDTO.providerSubtitle;
        this.joinedProviderSubtitle = providerCompactDTO.joinedProviderSubtitle;
        this.singleRowHeightPoint = providerCompactDTO.singleRowHeightPoint;
        this.multiRowHeightPoint = providerCompactDTO.multiRowHeightPoint;
        this.singleImageUrl = providerCompactDTO.singleImageUrl;
        this.singleSelectedImageUrl = providerCompactDTO.singleSelectedImageUrl;
        this.multiImageUrl = providerCompactDTO.multiImageUrl;
        this.multiSelectedImageUrl = providerCompactDTO.multiSelectedImageUrl;
        this.singleJoinedImageUrl = providerCompactDTO.singleJoinedImageUrl;
        this.singleJoinedSelectedImageUrl = providerCompactDTO.singleJoinedSelectedImageUrl;
        this.multiJoinedImageUrl = providerCompactDTO.multiJoinedImageUrl;
        this.multiJoinedSelectedImageUrl = providerCompactDTO.multiJoinedSelectedImageUrl;
        this.tradeButtonImageUrl = providerCompactDTO.tradeButtonImageUrl;
        this.tradeButtonSelectedImageUrl = providerCompactDTO.tradeButtonSelectedImageUrl;
        this.tileImageUrl = providerCompactDTO.tileImageUrl;
        this.tileJoinedImageUrl = providerCompactDTO.tileJoinedImageUrl;
        this.timerImageUrl = providerCompactDTO.timerImageUrl;
        this.volumeRestrictionEnabled = providerCompactDTO.volumeRestrictionEnabled;
        this.tradeRestrictionStartHourUtc = providerCompactDTO.tradeRestrictionStartHourUtc;
        this.tradeRestrictionEndHourUtc = providerCompactDTO.tradeRestrictionEndHourUtc;
        this.appiTunesUrl = providerCompactDTO.appiTunesUrl;
        this.iosAppSecurityPageUrlTemplate = providerCompactDTO.iosAppSecurityPageUrlTemplate;
        this.helpVideoText = providerCompactDTO.helpVideoText;
        this.ruleText = providerCompactDTO.ruleText;
        this.hasHelpVideo = providerCompactDTO.hasHelpVideo;
        this.wizardUrl = providerCompactDTO.wizardUrl;
        this.wizardTitle = providerCompactDTO.wizardTitle;
        this.wizardImageUrl = providerCompactDTO.wizardImageUrl;
        this.ctaLocationTags = providerCompactDTO.ctaLocationTags;
        this.currencyDisplay = providerCompactDTO.currencyDisplay;
        this.currencyISO = providerCompactDTO.currencyISO;

        PortfolioCompactDTO associatedPortfolio = providerCompactDTO.associatedPortfolio;
        if (associatedPortfolio != null)
        {
            this.associatedPortfolioId = associatedPortfolio.getPortfolioId();
            portfolioCompactCache.put(associatedPortfolioId, associatedPortfolio);
        }
        else
        {
            this.associatedPortfolioId = null;
        }

        this.specificKnowledge = providerCompactDTO.specificKnowledge;
        this.specificResources = providerCompactDTO.specificResources;
    }

    @Nullable ProviderCompactDTO create(@NotNull PortfolioCompactCache portfolioCompactCache)
    {
        return populate(new ProviderCompactDTO(), portfolioCompactCache);
    }



    @Nullable <T extends ProviderCompactDTO> T populate(
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

        empty.specificKnowledge = specificKnowledge;
        empty.specificResources = specificResources;

        return empty;
    }
}
