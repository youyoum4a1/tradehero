package com.ayondo.academy.api.leaderboard.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.key.CompetitionId;
import com.ayondo.academy.api.leaderboard.key.PagedLeaderboardKey;
import com.ayondo.academy.api.leaderboard.key.PerPagedLeaderboardKey;

public class CompetitionLeaderboardId extends PerPagedLeaderboardKey
{
    @NonNull public final Integer providerId;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardId(int providerId, int competitionId)
    {
        this(providerId, competitionId, null, null);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, @Nullable Integer page)
    {
        this(providerId, competitionId, page, null);
    }

    public CompetitionLeaderboardId(@NonNull CompetitionLeaderboardId other, @Nullable Integer page)
    {
        this(other.providerId, other.id, page, other.perPage);
    }

    public CompetitionLeaderboardId(int providerId, int competitionId, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(competitionId, page, perPage);
        this.providerId = providerId;
    }
    //</editor-fold>

    @NonNull public ProviderId getProviderId()
    {
        return new ProviderId(this.providerId);
    }

    @NonNull public CompetitionId getCompetitionId()
    {
        return new CompetitionId(this.id);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ providerId.hashCode();
    }

    @Override public boolean equalFields(@NonNull PerPagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof CompetitionLeaderboardId
                && equalFields((CompetitionLeaderboardId) other);
    }

    protected boolean equalFields(@NonNull CompetitionLeaderboardId other)
    {
        return super.equalFields(other)
                && this.providerId.equals(other.providerId);
    }

    @NonNull @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new CompetitionLeaderboardId(this, page);
    }
}
