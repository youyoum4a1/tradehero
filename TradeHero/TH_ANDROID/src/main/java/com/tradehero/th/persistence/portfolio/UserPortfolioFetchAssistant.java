package com.tradehero.th.persistence.portfolio;

import android.content.Context;
import com.tradehero.common.persistence.BasicFetchAssistant;
import com.tradehero.common.persistence.FetchAssistant;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileFetchAssistant;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class UserPortfolioFetchAssistant extends BasicFetchAssistant<UserBaseKey, OwnedPortfolioId>
    implements FetchAssistant.OnInfoFetchedListener<UserBaseKey, UserProfileDTO>
{
    private final UserProfileFetchAssistant userProfileFetchAssistant;

    //<editor-fold desc="Constructors">
    public UserPortfolioFetchAssistant(@NotNull Context context, List<UserBaseKey> keysToFetch)
    {
        super(keysToFetch);
        this.userProfileFetchAssistant = new UserProfileFetchAssistant(context, keysToFetch);
        this.userProfileFetchAssistant.setListener(this);
    }
    //</editor-fold>

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
                    this.fetched.put(entry.getKey(), new OwnedPortfolioId(entry.getKey().key, entry.getValue().portfolio.id));
                }
            }
            notifyListener();
        }
    }
}
