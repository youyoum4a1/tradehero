package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class UserMessagingRelationshipCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, UserMessagingRelationshipDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public UserMessagingRelationshipCacheRx(
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<UserMessagingRelationshipDTO> fetch(@NotNull UserBaseKey key)
    {
        return messageServiceWrapper.getMessagingRelationgshipStatusRx(key);
    }

    private void markIsHero(@NotNull UserBaseKey heroId)
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

    public void markIsFreeHero(@NotNull UserBaseKey heroId)
    {
        markIsHero(heroId);
        UserMessagingRelationshipDTO cached = getValue(heroId);
        if (cached != null)
        {
            cached.freeFollow = true;
        }
    }

    public void markIsPremiumHero(@NotNull UserBaseKey heroId)
    {
        markIsHero(heroId);
        UserMessagingRelationshipDTO cached = getValue(heroId);
        if (cached != null)
        {
            cached.freeFollow = false;
        }
    }

    public void markNotHero(@NotNull UserBaseKey formerHeroId)
    {
        UserMessagingRelationshipDTO cached = getValue(formerHeroId);
        if (cached != null)
        {
            cached.isHero = false;
        }
    }
}
