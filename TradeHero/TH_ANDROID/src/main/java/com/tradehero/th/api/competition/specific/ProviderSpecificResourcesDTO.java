package com.tradehero.th.api.competition.specific;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProviderSpecificResourcesDTO
{
    public int mainCompetitionFragmentTitleResId;
    public int notJoinedBannerImageResId;
    public int joinedBannerImageResId;

    public int tradeNowBtnImageResId;

    public int helpVideoListFragmentTitleResId;
    public int helpVideoLinkBackgroundResId;
    public int helpVideoLinkTextColourResId;

    public int timedHeaderLeaderboardTitleResId;
    public int competitionPortfolioTitleResId;

    public int securityListFragmentTitleResId;

    //<editor-fold desc="Constructors">
    public ProviderSpecificResourcesDTO()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore public int getJoinBannerResId(boolean isJoined)
    {
        return isJoined ? joinedBannerImageResId : notJoinedBannerImageResId;
    }

    @Override public String toString()
    {
        return "ProviderSpecificResourcesDTO{" +
                "mainCompetitionFragmentTitleResId=" + mainCompetitionFragmentTitleResId +
                ", notJoinedBannerImageResId=" + notJoinedBannerImageResId +
                ", joinedBannerImageResId=" + joinedBannerImageResId +
                ", tradeNowBtnImageResId=" + tradeNowBtnImageResId +
                ", helpVideoListFragmentTitleResId=" + helpVideoListFragmentTitleResId +
                ", helpVideoLinkBackgroundResId=" + helpVideoLinkBackgroundResId +
                ", helpVideoLinkTextColourResId=" + helpVideoLinkTextColourResId +
                ", timedHeaderLeaderboardTitleResId=" + timedHeaderLeaderboardTitleResId +
                ", competitionPortfolioTitleResId=" + competitionPortfolioTitleResId +
                ", securityListFragmentTitleResId=" + securityListFragmentTitleResId +
                '}';
    }
}
