package com.tradehero.th.models.provider;

/**
 * Created by xavier on 1/29/14.
 */
public class ProviderSpecificResourcesDTO
{
    public static final String TAG = ProviderSpecificResourcesDTO.class.getSimpleName();

    public int mainCompetitionFragmentTitleResId;
    public int helpVideoListFragmentTitleResId;
    public int helpVideoLinkBackgroundResId;
    public int timedHeaderLeaderboardTitleResId;
    public int competitionPortfolioTitleResId;
    public int securityListFragmentTitleResId;

    //<editor-fold desc="Constructors">
    public ProviderSpecificResourcesDTO()
    {
        super();
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "ProviderSpecificResourcesDTO{" +
                "mainCompetitionFragmentTitleResId=" + mainCompetitionFragmentTitleResId +
                ", helpVideoListFragmentTitleResId=" + helpVideoListFragmentTitleResId +
                ", helpVideoLinkBackgroundResId=" + helpVideoLinkBackgroundResId +
                ", timedHeaderLeaderboardTitleResId=" + timedHeaderLeaderboardTitleResId +
                ", competitionPortfolioTitleResId=" + competitionPortfolioTitleResId +
                ", securityListFragmentTitleResId=" + securityListFragmentTitleResId +
                '}';
    }
}
