package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserSearchResultDTO;
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
