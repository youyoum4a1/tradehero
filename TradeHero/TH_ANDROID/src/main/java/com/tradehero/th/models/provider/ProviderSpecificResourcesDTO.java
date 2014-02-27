package com.tradehero.th.models.provider;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by xavier on 1/29/14.
 */
public class ProviderSpecificResourcesDTO
{
    public static final String TAG = ProviderSpecificResourcesDTO.class.getSimpleName();

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
