package com.tradehero.th.api.leaderboard.competition;

import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.competition.CompetitionId;
import com.tradehero.th.api.competition.ProviderId;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionLeaderboardId implements DTOKey
{
    public static final String TAG = CompetitionLeaderboardId.class.getSimpleName();

    public final int providerId;
    public final int competitionId;
    public final Integer page;
    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardId(int providerId, int competitionId)
    {
        this(providerId, competitionId, null, null);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, Integer page)
    {
        this(providerId, competitionId, page, null);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, Integer page, Integer perPage)
    {
        this.providerId = providerId;
        this.competitionId = competitionId;
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    public ProviderId getProviderId()
    {
        return new ProviderId(this.providerId);
    }

    public CompetitionId getCompetitionId()
    {
        return new CompetitionId(this.competitionId);
    }

    @Override public int hashCode()
    {
         return new Integer(providerId).hashCode() ^
                new Integer(competitionId).hashCode() ^
                 (page == null ? new Integer(0) : page).hashCode() ^
                 (perPage == null ? new Integer(0) : perPage).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return super.equals(other);
    }

    public boolean equals(CompetitionLeaderboardId other)
    {
        return other != null &&
                this.providerId == other.providerId &&
                this.competitionId == other.competitionId &&
                (this.page == null ? other.page == null : this.page.equals(other.page)) ^
                (this.perPage == null ? other.perPage == null : this.perPage.equals(other.perPage));
    }
}
