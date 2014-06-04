package com.tradehero.th.api.position;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.UserBaseKey;

public class OwnedPositionId extends OwnedPortfolioId implements PositionDTOKey, DTO
{
    public final static String BUNDLE_KEY_POSITION_ID = OwnedPositionId.class.getName() + ".positionId";

    public final Integer positionId;

    //<editor-fold desc="Constructors">
    public OwnedPositionId(Integer userId, Integer portfolioId, Integer positionId)
    {
        super(userId, portfolioId);
        this.positionId = positionId;
    }

    public OwnedPositionId(UserBaseKey userBaseKey, PortfolioId portfolioId, Integer positionId)
    {
        super(userBaseKey, portfolioId);
        this.positionId = positionId;
    }

    public OwnedPositionId(Bundle args)
    {
        super(args);
        this.positionId = args.containsKey(BUNDLE_KEY_POSITION_ID) ? args.getInt(BUNDLE_KEY_POSITION_ID) : null;
    }

    public OwnedPositionId(OwnedPositionId ownedPositionId)
    {
        super(ownedPositionId.getUserBaseKey(), ownedPositionId.getPortfolioIdKey());
        this.positionId = ownedPositionId.positionId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (positionId == null ? 0 : positionId.hashCode());
    }

    public boolean equals(OwnedPositionId other)
    {
        return (other != null) &&
                super.equals(other) &&
                (positionId == null ? other.positionId == null : positionId.equals(other.positionId));
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

    @JsonIgnore @Override public boolean isValid()
    {
        return super.isValid() && this.positionId != null;
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_POSITION_ID, positionId);
    }

    @JsonIgnore public boolean isLocked()
    {
        return this.positionId < 0;
    }

    @Override public String toString()
    {
        return "OwnedPositionId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                ", positionId=" + positionId +
                '}';
    }
}
