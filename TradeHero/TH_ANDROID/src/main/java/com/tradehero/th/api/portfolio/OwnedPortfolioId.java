package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OwnedPortfolioId  implements Comparable, GetPositionsDTOKey
{
    public final static String BUNDLE_KEY_USER_ID = OwnedPortfolioId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_PORTFOLIO_ID = OwnedPortfolioId.class.getName() + ".portfolioId";

    @NotNull public final Integer userId;
    @NotNull public final Integer portfolioId;

    //<editor-fold desc="Constructors">
    private OwnedPortfolioId()
    {
        // Do not remove. Exists only for JSON deserialiser
        super();
        this.userId = -1;
        this.portfolioId = -1;
    }

    public OwnedPortfolioId(final int userId, final int portfolioId)
    {
        this.userId = userId;
        this.portfolioId = portfolioId;
    }

    public OwnedPortfolioId(@NotNull OwnedPortfolioId ownedPortfolioId)
    {
        this.userId = ownedPortfolioId.userId;
        this.portfolioId = ownedPortfolioId.portfolioId;
    }

    public OwnedPortfolioId(@NotNull Bundle args)
    {
        this.userId = args.getInt(BUNDLE_KEY_USER_ID);
        this.portfolioId = args.getInt(BUNDLE_KEY_PORTFOLIO_ID);
    }
    //</editor-fold>

    public static boolean isOwnedPortfolioId(@NotNull Bundle args)
    {
        return args.containsKey(BUNDLE_KEY_USER_ID) &&
                args.containsKey(BUNDLE_KEY_PORTFOLIO_ID);
    }

    @Override public int hashCode()
    {
        return userId.hashCode() ^ portfolioId.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return getClass().isInstance(other) && equals(getClass().cast(other));
    }

    public boolean equals(@NotNull OwnedPortfolioId other)
    {
        return userId.equals(other.userId) &&
                portfolioId.equals(other.portfolioId);
    }

    @Override public int compareTo(@NotNull Object other)
    {
        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
    }

    public int compareTo(@NotNull OwnedPortfolioId other)
    {
        if (this == other)
        {
            return 0;
        }

        int exchangeComp = userId.compareTo(other.userId);
        if (exchangeComp != 0)
        {
            return exchangeComp;
        }

        return portfolioId.compareTo(other.portfolioId);
    }

    @JsonIgnore public boolean isValid()
    {
        return userId > 0 && portfolioId > 0;
    }

    protected void putParameters(@NotNull Bundle args)
    {
        args.putInt(BUNDLE_KEY_USER_ID, userId);
        args.putInt(BUNDLE_KEY_PORTFOLIO_ID, portfolioId);
    }

    @JsonIgnore @NotNull public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @JsonIgnore @NotNull public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore @NotNull public PortfolioId getPortfolioIdKey()
    {
        return new PortfolioId(portfolioId);
    }

    @Override @NotNull public String toString()
    {
        return "OwnedPortfolioId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                '}';
    }
}
