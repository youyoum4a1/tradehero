package com.tradehero.th.persistence.user;

import android.content.Context;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.DTOFetchAssistantNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class UserProfileFetchAssistant extends DTOFetchAssistantNew<UserBaseKey, UserProfileDTO>
{
    private final Context context;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    public UserProfileFetchAssistant(final Context context, List<UserBaseKey> keysToFetch)
    {
        super(keysToFetch);
        this.context = context;
        DaggerUtils.inject(this);
    }

    //<editor-fold desc="DTOCache.Listener<UserBaseKey, UserProfileDTO>">
    @Override public void onErrorThrown(final UserBaseKey key, final Throwable error)
    {
        super.onErrorThrown(key, error);
        THToast.show(context.getString(R.string.error_fetch_user_profile));
    }
    //</editor-fold>

    @Override protected DTOCacheNew<UserBaseKey, UserProfileDTO> getCache()
    {
        return userProfileCache.get();
    }
}
