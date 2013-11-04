package com.tradehero.th.persistence.portfolio;

import android.content.Context;
import com.tradehero.common.persistence.BasicFetchAssistant;
import com.tradehero.common.persistence.FetchAssistant;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileAssistant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 4:28 PM To change this template use File | Settings | File Templates. */
public class UserPortfolioFetchAssistant extends BasicFetchAssistant<UserBaseKey, OwnedPortfolioId>
    implements FetchAssistant.OnInfoFetchedListener<UserBaseKey, UserProfileDTO>
{
    public static final String TAG = UserPortfolioFetchAssistant.class.getSimpleName();

    private final UserProfileAssistant userProfileAssistant;
    private final Map<UserBaseKey, OwnedPortfolioId> userPortfolios;

    public UserPortfolioFetchAssistant(Context context, List<UserBaseKey> keysToFetch)
    {
        super(keysToFetch);
        this.userProfileAssistant = new UserProfileAssistant(context, keysToFetch);
        userPortfolios = new HashMap<>();
        if (keysToFetch != null)
        {
            for (UserBaseKey key: keysToFetch)
            {
                if (key != null)
                {
                    userPortfolios.put(key, null);
                }
            }
        }
    }

    @Override public void execute(boolean force)
    {
        this.userProfileAssistant.execute(force);
    }

    @Override public void onInfoFetched(Map<UserBaseKey, UserProfileDTO> fetched, boolean isDataComplete)
    {
        if (fetched != null)
        {
            for (Map.Entry<UserBaseKey, UserProfileDTO> entry: fetched.entrySet())
            {
                if (userPortfolios.containsKey(entry.getKey()) &&
                        entry.getValue() != null &&
                        entry.getValue().portfolio != null)
                {
                    userPortfolios.put(entry.getKey(), new OwnedPortfolioId(entry.getKey(), entry.getValue().portfolio));
                }
            }
            notifyListener();
        }
    }
}
