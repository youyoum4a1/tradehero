package com.androidth.general.persistence.social;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.social.HeroDTO;
import com.androidth.general.api.social.HeroDTOList;
import com.androidth.general.api.social.key.FollowerHeroRelationId;
import com.androidth.general.api.users.UserBaseKey;
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
