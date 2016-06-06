package com.androidth.general.api.portfolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.users.UserBaseKey;

public class OwnedPortfolioId implements Comparable, GetPositionsDTOKey
{
    public final static String BUNDLE_KEY_USER_ID = OwnedPortfolioId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_PORTFOLIO_ID = OwnedPortfolioId.class.getName() + ".portfolioId";

    @NonNull public final Integer userId;
    @NonNull public final Integer portfolioId;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") private OwnedPortfolioId()
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

    public OwnedPortfolioId(@NonNull OwnedPortfolioId ownedPortfolioId)
    {
        this.userId = ownedPortfolioId.userId;
        this.portfolioId = ownedPortfolioId.portfolioId;
    }

    public OwnedPortfolioId(@NonNull Bundle args)
    {
        this.userId = args.getInt(BUNDLE_KEY_USER_ID);
        this.portfolioId = args.getInt(BUNDLE_KEY_PORTFOLIO_ID);
    }
    //</editor-fold>

    public static boolean isOwnedPortfolioId(@NonNull Bundle args)
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
        if (other == this)
        {
            return true;
        }
        return (other instanceof OwnedPortfolioId)
                && equalFields((OwnedPortfolioId) other);
    }

    protected boolean equalFields(@NonNull OwnedPortfolioId other)
    {
        return userId.equals(other.userId) &&
                portfolioId.equals(other.portfolioId);
    }

    @Override public int compareTo(@NonNull Object other)
    {
        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
    }

    protected int compareTo(@NonNull OwnedPortfolioId other)
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

    public static boolean isValid(@NonNull Bundle args)
    {
        return args.containsKey(BUNDLE_KEY_USER_ID)
                && args.getInt(BUNDLE_KEY_USER_ID, -1) > 0
                && args.containsKey(BUNDLE_KEY_PORTFOLIO_ID)
                && args.getInt(BUNDLE_KEY_PORTFOLIO_ID, -1) > 0;
    }

    protected void putParameters(@NonNull Bundle args)
    {
        args.putInt(BUNDLE_KEY_USER_ID, userId);
        args.putInt(BUNDLE_KEY_PORTFOLIO_ID, portfolioId);
    }

    @JsonIgnore @NonNull public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @JsonIgnore @NonNull public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    @JsonIgnore @NonNull public PortfolioId getPortfolioIdKey()
    {
        return new PortfolioId(portfolioId);
    }

    @Override @NonNull public String toString()
    {
        return "OwnedPortfolioId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                '}';
    }
}
