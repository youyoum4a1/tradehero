package com.tradehero.th.api.portfolio;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;

/**
 * This compound object allows the definition of a comparable mechanism, to order the list of PortfolioDTOs in the list.
 *
 * Created with IntelliJ IDEA. User: xavier Date: 10/25/13 Time: 11:52 AM To change this template use File | Settings | File Templates.
 */
public class DisplayablePortfolioDTO implements Comparable
{
    public static final String TAG = DisplayablePortfolioDTO.class.getSimpleName();

    @Inject /*@Named("CurrentUser")*/ public static UserBaseDTO currentUserBase;

    public OwnedPortfolioId ownedPortfolioId;
    public UserBaseDTO userBaseDTO;
    public PortfolioDTO portfolioDTO;

    //<editor-fold desc="Constructors">
    public DisplayablePortfolioDTO()
    {
        super();
    }

    public DisplayablePortfolioDTO(OwnedPortfolioId ownedPortfolioId)
    {
        this.ownedPortfolioId = ownedPortfolioId;
    }

    public DisplayablePortfolioDTO(OwnedPortfolioId ownedPortfolioId, UserBaseDTO userBaseDTO, PortfolioDTO portfolioDTO)
    {
        this.ownedPortfolioId = ownedPortfolioId;
        this.userBaseDTO = userBaseDTO;
        this.portfolioDTO = portfolioDTO;
    }
    //</editor-fold>

    public void populate(UserProfileCache userProfileCache, PortfolioCache portfolioCache)
    {
        populate(userProfileCache);
        populate(portfolioCache);
    }

    public void populate(UserProfileCache userProfileCache)
    {
        this.userBaseDTO = userProfileCache.get(ownedPortfolioId.getUserBaseKey());
    }

    public void populate(PortfolioCache portfolioCache)
    {
        this.portfolioDTO = portfolioCache.get(ownedPortfolioId);
    }

    public boolean isPopulated()
    {
        return ownedPortfolioId != null && userBaseDTO != null && portfolioDTO != null;
    }

    public boolean isValid()
    {
        return isPopulated() &&
                userBaseDTO.id == ownedPortfolioId.userId &&
                portfolioDTO.id == ownedPortfolioId.portfolioId;
    }

    public boolean isUserCurrentUser()
    {
        return currentUserBase.equals(userBaseDTO);
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof DisplayablePortfolioDTO) && equals((DisplayablePortfolioDTO) other);
    }

    public boolean equals(DisplayablePortfolioDTO other)
    {
        return (other != null) &&
                (ownedPortfolioId == null ? other.ownedPortfolioId == null : ownedPortfolioId.equals(other.ownedPortfolioId));
    }

    @Override public int hashCode()
    {
        return ownedPortfolioId == null ? 0 : ownedPortfolioId.hashCode();
    }

    /**
     * In this implementation, the natural order is:
     * a- null first
     * b- all those with null userBaseDTO
     * c- all those with null userBaseDTO.getBaseKey()
     * d- all those with userBaseDTO as current user
     *    da- first the default portfolio
     *    db- then ordered by creation date:
     *       dba- null first
     *       dbb- then older
     *       dbc- then newer
     * e- all those with userBaseDTO as not the current user
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

        if (other instanceof DisplayablePortfolioDTO)
        {
            return compareTo((DisplayablePortfolioDTO) other);
        }

        return other.getClass().getName().compareTo(DisplayablePortfolioDTO.class.getName());
    }

    public int compareTo(DisplayablePortfolioDTO other)
    {

        if (other == null)
        {
            return 1; // a-
        }

        if (ownedPortfolioId == null)
        {
            return other.ownedPortfolioId == null ? 0 : -1;
        }
        if (other.ownedPortfolioId == null)
        {
            return 1;
        }

        return ownedPortfolioId.compareTo(other.ownedPortfolioId);

        /*
        if (userBaseDTO == null)
        {
            return other.userBaseDTO == null ? 0 : -1;
        }
        if (other.userBaseDTO == null)
        {
            return 1;
        }

        if (portfolioDTO == null)
        {
            return other.portfolioDTO == null ? 0 : -1;
        }
        if (other.portfolioDTO == null)
        {
            return 1;
        }

        if (userBaseDTO.equals(currentUserBase))
        {
            if (!other.userBaseDTO.equals(currentUserBase))
            {
                return -1; // d-
            }
            if (portfolioDTO.isDefault())
            {
                return other.portfolioDTO.isDefault() ? 0 : -1; // da-
            }
            if (other.portfolioDTO.isDefault())
            {
                return 1; // da-
            }
            if (portfolioDTO.creationDate == null)
            {
                return other.portfolioDTO.creationDate == null ? 0 : -1; // dba-
            }
            if (other.portfolioDTO.creationDate == null)
            {
                return 1;
            }
            return portfolioDTO.creationDate.compareTo(other.portfolioDTO.creationDate); // dbb- dbc-
        }
        else if (other.userBaseDTO.equals(currentUserBase))
        {
            return 1; // d-
        }
        else
        {
            int firstNameComp = userBaseDTO.firstName == null ?
                    (other.userBaseDTO.firstName == null ? 0 : -1) :
                    userBaseDTO.firstName.compareTo(other.userBaseDTO.firstName);
            if (firstNameComp != 0)
            {
                return firstNameComp; // ea-
            }
            if (portfolioDTO.isDefault())
            {
                return other.portfolioDTO.isDefault() ? 0 : -1; // da-
            }
            if (other.portfolioDTO.isDefault())
            {
                return 1; // da-
            }
            if (portfolioDTO.creationDate == null)
            {
                return other.portfolioDTO.creationDate == null ? 0 : -1; // eaa-
            }
            if (other.portfolioDTO.creationDate == null)
            {
                return 1;
            }
            return portfolioDTO.creationDate.compareTo(other.portfolioDTO.creationDate); // eaa-
        }
        */
    }
}
