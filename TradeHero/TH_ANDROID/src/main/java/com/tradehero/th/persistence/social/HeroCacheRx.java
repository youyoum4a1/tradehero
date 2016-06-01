package com.ayondo.academy.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.social.HeroDTO;
import com.ayondo.academy.api.social.HeroDTOList;
import com.ayondo.academy.api.social.key.FollowerHeroRelationId;
import com.ayondo.academy.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class HeroCacheRx extends BaseDTOCacheRx<FollowerHeroRelationId, HeroDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public HeroCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull UserBaseKey followerId, @NonNull HeroDTOList values)
    {
        for (HeroDTO value: values)
        {
            onNext(value.getHeroId(followerId), value);
        }
    }
}
