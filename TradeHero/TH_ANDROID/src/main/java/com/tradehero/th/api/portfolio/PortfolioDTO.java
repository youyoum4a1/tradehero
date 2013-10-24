package com.tradehero.th.api.portfolio;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.quote.UpdatePricesQuoteDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:06 PM Copyright (c) TradeHero */
public class PortfolioDTO extends PortfolioCompactDTO implements Comparable, DTO
{
    public double initialCash;
    public Date creationDate;
    public String description;
    public String Currency;

    public Double roiSinceInception;
    public Double roiSinceInceptionAnnualized;
    public double plSinceInception;

    public Double roiM2D;
    public Double roiM2DAnnualized;
    public double plM2D;

    public Double roiQ2D;
    public Double roiQ2DAnnualized;
    public double plQ2D;

    public Double roiY2D;
    public Double roiY2DAnnualized;
    public double plY2D;

    public List<UpdatePricesQuoteDTO> yahooSymbols;
    public Date markingAsOfUtc;

    public int countTrades;
    public int countExchanges;

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

        if (other instanceof PortfolioDTO)
        {
            return compareTo((PortfolioDTO) other);
        }

        return other.getClass().getName().compareTo(PortfolioDTO.class.getName());
    }

    public int compareTo(PortfolioDTO other)
    {
        int parentComp = super.compareTo(other);

        if (parentComp != 0)
        {
            return parentComp;
        }

        if (other == null)
        {
            return 1; // a-
        }

        // We take shortcuts because we have already super.compared
        if (ownerDTO == null && other.ownerDTO == null)
        {
            return 0; // b-
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
            if (other.isDefault())
            {
                return 1; // da-
            }
            if (creationDate == null)
            {
                return other.creationDate == null ? 0 : -1; // dba-
            }
            if (other.creationDate == null)
            {
                return 1;
            }
            return creationDate.compareTo(other.creationDate); // dbb- dbc-
        }
        else if (other.ownerDTO.equals(currentUserBase))
        {
            return -1; // d-
        }
        else
        {
            int firstNameComp = ownerDTO.firstName == null ? (other.ownerDTO.firstName == null ? 0 : -1) : ownerDTO.firstName.compareTo(other.ownerDTO.firstName);
            if (firstNameComp != 0)
            {
                return firstNameComp; // ea-
            }
            if (creationDate == null)
            {
                return other.creationDate == null ? 0 : -1; // eaa-
            }
            if (other.creationDate == null)
            {
                return 1;
            }
            return creationDate.compareTo(other.creationDate); // eaa-
        }
    }
}
