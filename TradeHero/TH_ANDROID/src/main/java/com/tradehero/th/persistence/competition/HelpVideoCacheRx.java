package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class HelpVideoCacheRx extends BaseDTOCacheRx<HelpVideoId, HelpVideoDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 200;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 20;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoCacheRx()
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
    }
    //</editor-fold>

    public void onNext(@NotNull List<? extends HelpVideoDTO> videoDTOList)
    {
        for (HelpVideoDTO videoDTO : videoDTOList)
        {
            onNext(videoDTO.getHelpVideoId(), videoDTO);
        }
    }
}
