package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.users.UserBaseDTO;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:05 PM Copyright (c) TradeHero */
public class PortfolioCompactDTO implements Comparable, DTO
{
    public static final String DEFAULT_TITLE = "Default";

    public int id;
    public Integer providerId;
    public String title;
    public double cashBalance;
    public double totalValue;
    public double totalExtraCashPurchased;
    public double totalExtraCashGiven;

    @Inject /*@Named("CurrentUser")*/ public static UserBaseDTO currentUserBase;

    protected UserBaseDTO ownerDTO;

    //<editor-fold desc="Constructors">
    public PortfolioCompactDTO()
    {
    }

    public PortfolioId getPortfolioId()
    {
        return new PortfolioId(id);
    }
    //</editor-fold>

    public boolean isDefault()
    {
        return DEFAULT_TITLE.equals(title);
    }

    public void setOwnerDTO(UserBaseDTO ownerDTO)
    {
        this.ownerDTO = ownerDTO;
    }

    public boolean isOwnerCurrentUser()
    {
        return currentUserBase.equals(ownerDTO);
    }

    public double getTotalExtraCash()
    {
        return totalExtraCashGiven + totalExtraCashPurchased;
    }

    /**
     * In this implementation, the natural order is:
     * a- null first
     * b- all those with null ownerDTO
     * c- all those with null ownerDTO.getBaseKey()
     * d- all those with ownerDTO as current user
     *    da- first the default portfolio
     *    db- then ordered by creation date:
     *       dba- null first
     *       dbb- then older
     *       dbc- then newer
     * e- all those with ownerDTO as not the current user
     *    ea- ordered by user name natural order
     *       eaa- for each user, ordered by creation date, older first
     * @param other
     * @return
     */
    @Override public int compareTo(Object other)
    {
        if (other == null)
        {
            return 1;
        }

        if (other instanceof PortfolioCompactDTO)
        {
            return compareTo((PortfolioCompactDTO) other);
        }

        return other.getClass().getName().compareTo(PortfolioCompactDTO.class.getName());
    }

    public int compareTo(PortfolioCompactDTO other)
    {
        if (other == null)
        {
            return 1; // a-
        }

        if (ownerDTO == null)
        {
            return other.ownerDTO == null ? 0 : -1; // b-
        }

        if (other.ownerDTO == null)
        {
            return 1; // b-
        }

        if (ownerDTO.getBaseKey() == null)
        {
            return other.ownerDTO == null ? 1 : other.ownerDTO.getBaseKey() == null ? 0 : -1; // c-
        }

        if (other.ownerDTO.getBaseKey() == null)
        {
            return 1; // c-
        }

        if (ownerDTO.equals(currentUserBase))
        {
            if (!other.ownerDTO.equals(currentUserBase))
            {
                return -1; // d-
            }
            if (isDefault())
            {
                return other.isDefault() ? 0 : -1; // da-
            }
            return other.isDefault() ? 1 : 0;
        }
        else if (other.ownerDTO.equals(currentUserBase))
        {
            return 1; // d-
        }
        else
        {
            return ownerDTO.firstName == null ? (other.ownerDTO.firstName == null ? 0 : -1) : ownerDTO.firstName.compareTo(other.ownerDTO.firstName); // ea-
        }
    }
}