package com.ayondo.academy.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserSearchResultDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class UserSearchResultCacheRx extends BaseDTOCacheRx<UserBaseKey, UserSearchResultDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    //<editor-fold desc="Constructors">
    @Inject public UserSearchResultCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<UserSearchResultDTO> values)
    {
        for (UserSearchResultDTO userSearchResultDTO: values)
        {
            if (userSearchResultDTO != null && userSearchResultDTO.getUserBaseKey() != null)
            {
                onNext(userSearchResultDTO.getUserBaseKey(), userSearchResultDTO);
            }
        }
    }
}
