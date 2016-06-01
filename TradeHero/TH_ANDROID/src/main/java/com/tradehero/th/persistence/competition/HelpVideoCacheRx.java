package com.ayondo.academy.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.competition.HelpVideoDTO;
import com.ayondo.academy.api.competition.key.HelpVideoId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class HelpVideoCacheRx extends BaseDTOCacheRx<HelpVideoId, HelpVideoDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<? extends HelpVideoDTO> videoDTOList)
    {
        for (HelpVideoDTO videoDTO : videoDTOList)
        {
            onNext(videoDTO.getHelpVideoId(), videoDTO);
        }
    }
}
