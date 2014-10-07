package com.tradehero.th.api.competition.specific;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProviderSpecificResourcesDTO
{
    @StringRes public int mainCompetitionFragmentTitleResId;
    @DrawableRes public int notJoinedBannerImageResId;
    @DrawableRes public int joinedBannerImageResId;

    @DrawableRes public int tradeNowBtnImageResId;

    @StringRes public int helpVideoListFragmentTitleResId;
    @DrawableRes public int helpVideoLinkBackgroundResId;
    @ColorRes public int helpVideoLinkTextColourResId;

    @StringRes public int timedHeaderLeaderboardTitleResId;
    @StringRes public int competitionPortfolioTitleResId;

    @StringRes public int securityListFragmentTitleResId;

    //<editor-fold desc="Constructors">
    public ProviderSpecificResourcesDTO()
    {
        super();
    }
    //</editor-fold>

    @JsonIgnore @DrawableRes public int getJoinBannerResId(boolean isJoined)
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
