package com.tradehero.th.api.leaderboard.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;

public class CompetitionLeaderboardId implements DTOKey
{
    @NonNull public final Integer providerId;
    @NonNull public final Integer competitionId;
    @Nullable public final Integer page;
    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardId(int providerId, int competitionId)
    {
        this(providerId, competitionId, null, null);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, @Nullable Integer page)
    {
        this(providerId, competitionId, page, null);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, @Nullable Integer page, @Nullable Integer perPage)
    {
        this.providerId = providerId;
        this.competitionId = competitionId;
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    @NonNull public ProviderId getProviderId()
    {
        return new ProviderId(this.providerId);
    }

    @NonNull public CompetitionId getCompetitionId()
    {
        return new CompetitionId(this.competitionId);
    }

    @Override public int hashCode()
    {
         return providerId.hashCode() ^
                competitionId.hashCode() ^
                 (page == null ? Integer.valueOf(0) : page).hashCode() ^
                 (perPage == null ? Integer.valueOf(0) : perPage).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null
                && other.getClass().equals(getClass())
                && equalFields((CompetitionLeaderboardId) other);
    }

    protected boolean equalFields(CompetitionLeaderboardId other)
    {
        return other != null &&
                this.providerId.equals(other.providerId) &&
                this.competitionId.equals(other.competitionId) &&
                (this.page == null ? other.page == null : this.page.equals(other.page)) ^
                (this.perPage == null ? other.perPage == null : this.perPage.equals(other.perPage));
    }
}
