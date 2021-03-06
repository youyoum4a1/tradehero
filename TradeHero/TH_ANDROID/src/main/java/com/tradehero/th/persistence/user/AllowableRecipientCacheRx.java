package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache
public class AllowableRecipientCacheRx extends BaseDTOCacheRx<UserBaseKey, AllowableRecipientDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 300;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 3;

    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientCacheRx(
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull AllowableRecipientDTO value)
    {
        userMessagingRelationshipCache.get().onNext(value.user.getBaseKey(), value.relationship);
        super.onNext(key, value);
    }

    public void onNext(@NonNull List<AllowableRecipientDTO> allowableRecipientDTOs)
    {
        for (AllowableRecipientDTO allowableRecipientDTO : allowableRecipientDTOs)
        {
            onNext(allowableRecipientDTO.user.getBaseKey(), allowableRecipientDTO);
        }
    }
}
