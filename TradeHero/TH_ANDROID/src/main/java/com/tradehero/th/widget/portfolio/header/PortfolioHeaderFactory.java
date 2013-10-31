package com.tradehero.th.widget.portfolio.header;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by julien on 22/10/13
 * Singleton Class creating instances of PortfolioHeaderView based on which arguments are passed to the PositionListFragment
 */
@Singleton public class PortfolioHeaderFactory
{
    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;
    @Inject Lazy<UserProfileCache> userCache;

    public int layoutIdForArguments(Bundle args)
    {
        if (args == null)
        {
            throw new PortfolioHeaderFactoryException("Unable to build arguments from Bundle");
        }
        OwnedPortfolioId id = new OwnedPortfolioId(args);

        UserProfileDTO currentUser = userCache.get().get(currentUserBase.getBaseKey());

        int userId = id.getUserBaseKey().key;
        if (userId == currentUser.id)
        {
            return R.layout.portfolio_header_current_user_view;
        }
        else
        {
            return R.layout.portfolio_header_other_user_view;
        }
    }
}
