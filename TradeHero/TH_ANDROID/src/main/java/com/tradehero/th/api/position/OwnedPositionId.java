package com.tradehero.th.api.position;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import org.jetbrains.annotations.NotNull;

public class OwnedPositionId extends OwnedPortfolioId implements PositionDTOKey, DTO
{
    public final static String BUNDLE_KEY_POSITION_ID = OwnedPositionId.class.getName() + ".positionId";

    @NotNull public final Integer positionId;

    //<editor-fold desc="Constructors">
    public OwnedPositionId(int userId, int portfolioId, int positionId)
    {
        super(userId, portfolioId);
        this.positionId = positionId;
    }

    public OwnedPositionId(Bundle args)
    {
        super(args);
        this.positionId = args.getInt(BUNDLE_KEY_POSITION_ID);
    }
    //</editor-fold>

    public static boolean isOwnedPositionId(@NotNull Bundle args)
    {
        return isOwnedPortfolioId(args) &&
                args.containsKey(BUNDLE_KEY_POSITION_ID);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ positionId.hashCode();
    }

    public boolean equals(OwnedPositionId other)
    {
        return (other != null) &&
                super.equals(other) &&
                positionId.equals(other.positionId);
    }

    public int compareTo(OwnedPositionId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return positionId.compareTo(other.positionId);
    }

    @Override protected void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_POSITION_ID, positionId);
    }

    @JsonIgnore public boolean isLocked()
    {
        return this.positionId < 0;
    }

    @Override @NotNull public String toString()
    {
        return "OwnedPositionId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                ", positionId=" + positionId +
                '}';
    }
}
