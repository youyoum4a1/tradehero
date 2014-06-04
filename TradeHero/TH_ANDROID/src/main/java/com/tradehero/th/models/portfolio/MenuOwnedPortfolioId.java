package com.tradehero.th.models.portfolio;

import android.os.Bundle;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

public class MenuOwnedPortfolioId extends OwnedPortfolioId implements CharSequence
{
    public final String title;

    //<editor-fold desc="Constructors">
    public MenuOwnedPortfolioId(Integer userId, Integer portfolioId, String title)
    {
        super(userId, portfolioId);
        this.title = title;
    }

    public MenuOwnedPortfolioId(UserBaseKey userBaseKey, PortfolioId portfolioId, String title)
    {
        super(userBaseKey, portfolioId);
        this.title = title;
    }

    public MenuOwnedPortfolioId(OwnedPortfolioId ownedPortfolioId, String title)
    {
        super(ownedPortfolioId);
        this.title = title;
    }

    public MenuOwnedPortfolioId(UserBaseKey userBaseKey, PortfolioCompactDTO portfolioCompactDTO)
    {
        super(userBaseKey, portfolioCompactDTO);
        this.title = portfolioCompactDTO == null ? null : portfolioCompactDTO.title;
    }

    public MenuOwnedPortfolioId(OwnedPortfolioId ownedPortfolioId, PortfolioCompactDTO portfolioCompactDTO)
    {
        super(ownedPortfolioId);
        this.title = portfolioCompactDTO == null ? null : portfolioCompactDTO.title;
    }

    public MenuOwnedPortfolioId(UserBaseDTO userBaseDTO, PortfolioCompactDTO portfolioCompactDTO)
    {
        super(userBaseDTO, portfolioCompactDTO);
        this.title = portfolioCompactDTO == null ? null : portfolioCompactDTO.title;
    }

    public MenuOwnedPortfolioId(Bundle args, String title)
    {
        super(args);
        this.title = title;
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

    @Override public CharSequence subSequence(int start, int end)
    {
        return title == null ? null : title.subSequence(start, end);
    }

    @Override public String toString()
    {
        return title == null ? "" : title;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ title.hashCode();
    }

    @Override public boolean equals(OwnedPortfolioId other)
    {
        return getClass().isInstance(other) && equals(getClass().cast(other));
    }

    public boolean equals(MenuOwnedPortfolioId other)
    {
        return super.equals(other) && other != null &&
                (title == null ? other.title == null : title.equals(other.title));
    }
}
