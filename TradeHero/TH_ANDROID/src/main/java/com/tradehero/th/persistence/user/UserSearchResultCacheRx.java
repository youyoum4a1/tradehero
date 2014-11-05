package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class UserSearchResultCacheRx extends BaseDTOCacheRx<UserBaseKey, UserSearchResultDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 2;

    //<editor-fold desc="Constructors">
    @Inject public UserSearchResultCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
