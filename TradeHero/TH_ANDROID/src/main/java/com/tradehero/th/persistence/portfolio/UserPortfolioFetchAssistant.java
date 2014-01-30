package com.tradehero.th.persistence.portfolio;

import android.content.Context;
import com.tradehero.common.persistence.BasicFetchAssistant;
import com.tradehero.common.persistence.FetchAssistant;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileFetchAssistant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 4:28 PM To change this template use File | Settings | File Templates. */
public class UserPortfolioFetchAssistant extends BasicFetchAssistant<UserBaseKey, OwnedPortfolioId>
    implements FetchAssistant.OnInfoFetchedListener<UserBaseKey, UserProfileDTO>
{
    public static final String TAG = UserPortfolioFetchAssistant.class.getSimpleName();

    private final UserProfileFetchAssistant userProfileFetchAssistant;

    public UserPortfolioFetchAssistant(Context context, List<UserBaseKey> keysToFetch)
    {
        super(keysToFetch);
        this.userProfileFetchAssistant = new UserProfileFetchAssistant(context, keysToFetch);
        this.userProfileFetchAssistant.setListener(this);
    }

    @Override public void execute(boolean force)
    {
        this.userProfileFetchAssistant.execute(force);
    }

    @Override public void clear()
    {
        super.clear();
        this.userProfileFetchAssistant.setListener(null);
    }

    @Override public void onInfoFetched(Map<UserBaseKey, UserProfileDTO> fetched, boolean isDataComplete)
    {
        if (fetched != null)
        {
            for (Map.Entry<UserBaseKey, UserProfileDTO> entry: fetched.entrySet())
            {
                if (this.fetched.containsKey(entry.getKey()) &&
                        entry.getValue() != null &&
                        entry.getValue().portfolio != null)
                {
                    this.fetched.put(entry.getKey(), new OwnedPortfolioId(entry.getKey(), entry.getValue().portfolio));
                }
            }
            notifyListener();
        }
    }
}
