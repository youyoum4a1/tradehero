package com.tradehero.th.models.portfolio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;

public class MenuOwnedPortfolioId extends OwnedPortfolioId
        implements CharSequence
{
    @Nullable public final String title;

    //<editor-fold desc="Constructors">
    public MenuOwnedPortfolioId(int userId, int portfolioId, @Nullable String title)
    {
        super(userId, portfolioId);
        this.title = title;
    }

    public MenuOwnedPortfolioId(@NonNull UserBaseKey userBaseKey, @NonNull PortfolioCompactDTO portfolioCompactDTO)
    {
        super(userBaseKey.key, portfolioCompactDTO.id);
        this.title = portfolioCompactDTO.title;
    }

    public MenuOwnedPortfolioId(@NonNull OwnedPortfolioId ownedPortfolioId, @Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        super(ownedPortfolioId);
        this.title = portfolioCompactDTO == null ? null : portfolioCompactDTO.title;
    }
    //</editor-fold>

    //<editor-fold desc="CharSequence">
    @Override public int length()
    {
        return title == null ? 0 : title.length();
    }

    @Override public char charAt(int index)
    {
        return title == null ? 'a' : title.charAt(index);
    }

    @Override @Nullable public CharSequence subSequence(int start, int end)
    {
        return title == null ? null : title.subSequence(start, end);
    }

    @Override @NonNull public String toString()
    {
        return title == null ? "" : title;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode()
                ^ (title == null ? 0 : title.hashCode());
    }

    @Override public boolean equals(@NonNull OwnedPortfolioId other)
    {
        return getClass().isInstance(other) && equals(getClass().cast(other));
    }

    public boolean equals(@NonNull MenuOwnedPortfolioId other)
    {
        return super.equals(other) &&
                (title == null ? other.title == null : title.equals(other.title));
    }
}
