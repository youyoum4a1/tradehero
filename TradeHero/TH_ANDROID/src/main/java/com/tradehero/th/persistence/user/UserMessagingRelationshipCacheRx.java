package com.tradehero.th.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserMessagingRelationshipCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserMessagingRelationshipDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserMessagingRelationshipCacheRx(
            @NonNull MessageServiceWrapper messageServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserMessagingRelationshipDTO> fetch(@NonNull UserBaseKey key)
    {
        return messageServiceWrapper.getMessagingRelationshipStatusRx(key);
    }

    private void markIsHero(@NonNull UserBaseKey heroId)
    {
        UserMessagingRelationshipDTO cached = getValue(heroId);
        if (cached != null)
        {
            cached.isHero = true;
            if (cached.heroSince == null)
            {
                cached.heroSince = new Date();
            }
        }
    }

    public void markIsFreeHero(@NonNull UserBaseKey heroId)
    {
        markIsHero(heroId);
        UserMessagingRelationshipDTO cached = getValue(heroId);
        if (cached != null)
        {
            cached.freeFollow = true;
        }
    }

    public void markIsPremiumHero(@NonNull UserBaseKey heroId)
    {
        markIsHero(heroId);
        UserMessagingRelationshipDTO cached = getValue(heroId);
        if (cached != null)
        {
            cached.freeFollow = false;
        }
    }

    public void markNotHero(@NonNull UserBaseKey formerHeroId)
    {
        UserMessagingRelationshipDTO cached = getValue(formerHeroId);
        if (cached != null)
        {
            cached.isHero = false;
        }
    }
}
