package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class HelpVideoCache extends StraightDTOCacheNew<HelpVideoId, HelpVideoDTO>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public HelpVideoDTO fetch(@NotNull HelpVideoId key) throws Throwable
    {
        throw new RuntimeException();
    }
}
