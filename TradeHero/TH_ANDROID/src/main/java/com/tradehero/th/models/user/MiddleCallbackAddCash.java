package com.tradehero.th.models.user;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/21/14.
 */
public class MiddleCallbackAddCash extends MiddleCallbackUpdateUserProfile
{
    @Inject PortfolioCompactListCache portfolioCompactListCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;

    protected final OwnedPortfolioId ownedPortfolioId;

    public MiddleCallbackAddCash(OwnedPortfolioId ownedPortfolioId, Callback<UserProfileDTO> primaryCallback)
    {
        super(primaryCallback);
        this.ownedPortfolioId = ownedPortfolioId;
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        portfolioCompactListCache.invalidate(ownedPortfolioId.getUserBaseKey());
        portfolioCompactCache.invalidate(ownedPortfolioId.getPortfolioIdKey());
        portfolioCache.invalidate(ownedPortfolioId);
        super.success(userProfileDTO, response);
    }
}
