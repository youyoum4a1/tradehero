package com.tradehero.th.persistence.user;

import android.content.Context;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOFetchAssistant;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 3:42 PM To change this template use File | Settings | File Templates. */
public class UserProfileFetchAssistant extends DTOFetchAssistant<UserBaseKey, UserProfileDTO>
{
    public static final String TAG = UserProfileFetchAssistant.class.getSimpleName();

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

    @Override protected DTOCache<UserBaseKey, UserProfileDTO> getCache()
    {
        return userProfileCache.get();
    }
}
