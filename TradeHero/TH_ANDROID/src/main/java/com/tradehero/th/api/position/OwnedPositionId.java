package com.ayondo.academy.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;

public class OwnedPositionId extends OwnedPortfolioId implements PositionDTOKey, DTO
{
    public final static String BUNDLE_KEY_POSITION_ID = OwnedPositionId.class.getName() + ".positionId";

    @NonNull public final Integer positionId;

    //<editor-fold desc="Constructors">
    public OwnedPositionId(int userId, int portfolioId, int positionId)
    {
        super(userId, portfolioId);
        this.positionId = positionId;
    }

    public OwnedPositionId(@NonNull Bundle args)
    {
        super(args);
        this.positionId = args.getInt(BUNDLE_KEY_POSITION_ID);
    }
    //</editor-fold>

    public static boolean isOwnedPositionId(@NonNull Bundle args)
    {
        return isOwnedPortfolioId(args) &&
                args.containsKey(BUNDLE_KEY_POSITION_ID);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ positionId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull OwnedPortfolioId other)
    {
        return (other instanceof OwnedPositionId)
                && equalFields((OwnedPositionId) other);
    }

    protected boolean equalFields(@NonNull OwnedPositionId other)
    {
        return super.equalFields(other) &&
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

    public static boolean isValid(@NonNull Bundle args)
    {
        return OwnedPortfolioId.isValid(args)
                && args.containsKey(BUNDLE_KEY_POSITION_ID)
                && args.getInt(BUNDLE_KEY_POSITION_ID, -1) > 0;
    }

    @Override protected void putParameters(@NonNull Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_POSITION_ID, positionId);
    }

    @JsonIgnore public boolean isLocked()
    {
        return this.positionId < 0;
    }

    @Override @NonNull public String toString()
    {
        return "OwnedPositionId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                ", positionId=" + positionId +
                '}';
    }
}
