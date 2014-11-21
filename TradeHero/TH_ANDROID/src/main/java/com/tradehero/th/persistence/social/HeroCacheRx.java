package com.tradehero.th.persistence.social;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.social.FollowerHeroRelationIdList;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class HeroCacheRx extends BaseDTOCacheRx<FollowerHeroRelationId, HeroDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public HeroCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull UserBaseKey followerId, @NonNull HeroDTOList values)
    {
        for (HeroDTO value: values)
        {
            onNext(value.getHeroId(followerId), value);
        }
    }

    @Deprecated
    @NonNull HeroDTOList getValue(@NonNull FollowerHeroRelationIdList keys)
    {
        HeroDTOList list = new HeroDTOList();
        for (FollowerHeroRelationId key : keys)
        {
            list.add(getValue(key));
        }
        return list;
    }
}
