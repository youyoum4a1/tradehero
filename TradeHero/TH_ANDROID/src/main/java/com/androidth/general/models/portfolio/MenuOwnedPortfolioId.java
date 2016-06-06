package com.androidth.general.models.portfolio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.users.UserBaseKey;

public class MenuOwnedPortfolioId extends OwnedPortfolioId
        implements CharSequence
{
    @Nullable public String title;

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

    @Override protected boolean equalFields(@NonNull OwnedPortfolioId other)
    {
        return (other instanceof MenuOwnedPortfolioId)
                && equalFields((MenuOwnedPortfolioId) other);
    }

    protected boolean equalFields(@NonNull MenuOwnedPortfolioId other)
    {
        return super.equalFields(other) &&
                (title == null ? other.title == null : title.equals(other.title));
    }
}
