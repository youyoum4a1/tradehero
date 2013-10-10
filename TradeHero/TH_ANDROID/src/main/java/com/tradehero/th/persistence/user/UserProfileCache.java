package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton
public class UserProfileCache extends StraightDTOCache<Integer, UserBaseKey, UserProfileDTO>
{
    public static final String TAG = UserProfileCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<UserService> userService;

    //<editor-fold desc="Constructors">
    public UserProfileCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserProfileDTO fetch(UserBaseKey key)
    {
        try
        {
            return userService.get().getUser(key.makeKey());
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    public List<UserProfileDTO> getOrFetch(List<UserBaseKey> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserProfileDTO> userProfileDTOs = new ArrayList<>();
        if (baseKeys != null)
        {
            for(UserBaseKey baseKey: baseKeys)
            {
                userProfileDTOs.add(getOrFetch(baseKey, false));
            }
        }
        return userProfileDTOs;
    }
}
